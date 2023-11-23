package br.com.sankhya.bhz.cotacao;

import br.com.sankhya.bhz.utils.ErroUtils;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.Collection;

public class acaoGeraLiberacao implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        JapeWrapper cotDAO = JapeFactory.dao("CabecalhoCotacao"); //TGFCOT
        JapeWrapper libDAO = JapeFactory.dao("LiberacaoLimite"); //TSILIB
        JapeWrapper itcDAO = JapeFactory.dao("ItemCotacao"); //TGFITC
        JapeWrapper parDAO = JapeFactory.dao("Parceiro"); //TGFPAR
        if (contextoAcao.getLinhas().length > 1) {
            ErroUtils.disparaErro("Favor selecionar somente um registro !");
        }
        if (contextoAcao.confirmarSimNao("Confirma Solicitação de Aprovação ?", "Deseja solicitar aprovação?", 1)) {
            Registro[] linhas = contextoAcao.getLinhas();
            for (Registro linha : linhas) {
                DynamicVO cotVO = cotDAO.findOne("NUMCOTACAO=?",linha.getCampo("NUMCOTACAO"));
                BigDecimal numcotacao = cotVO.asBigDecimal("NUMCOTACAO");
                BigDecimal solicitante = AuthenticationInfo.getCurrent().getUserID();
                BigDecimal codparc = BigDecimal.ZERO;
                DynamicVO parVO = parDAO.findOne("CODPARC = (SELECT CODPARC FROM TGFITC WHERE NUMCOTACAO=? AND CODPROD=? AND MELHOR='S')",numcotacao,linha.getCampo("CODPROD"));
                if(parVO != null) {
                    codparc = parVO.asBigDecimalOrZero("CODPARC");
                }/*else {
                    ErroUtils.disparaErro("ESCOLHA QUAL MELHOR PREÇO PRIMEIRO");
                }*/
                DynamicVO itemcot = itcDAO.findOne("NUMCOTACAO=? AND CODPROD=? AND CODPARC=?",numcotacao,linha.getCampo("CODPROD"),codparc);

                BigDecimal codprod = itemcot.asBigDecimalOrZero("CODPROD");
                DynamicVO libVO = libDAO.findOne("NUCHAVE=? AND TABELA=? AND EVENTO=? AND SEQUENCIA=? AND NUCLL=?",numcotacao,"TGFITC",new BigDecimal(1095),codprod,codparc);
                if(libVO != null){
                    return;
                }
                    FluidCreateVO lib = libDAO.create();
                    lib.set("NUCHAVE",numcotacao);
                    lib.set("TABELA","TGFITC");
                    lib.set("EVENTO",new BigDecimal(1095));
                    lib.set("SEQUENCIA",codprod);
                    lib.set("NUCLL",codparc);
                    lib.set("CODUSUSOLICIT",solicitante);
                    lib.set("DHSOLICIT", TimeUtils.getNow());
                    lib.set("VLRLIMITE",itemcot.asBigDecimalOrZero("PRECO"));
                    lib.set("VLRATUAL",itemcot.asBigDecimalOrZero("PRECO"));
                    lib.set("NUCHAVE",numcotacao);

                    lib.save();


            }

        }
        contextoAcao.setMensagemRetorno("<b>SOLICITAÇÃO DE APROVAÇÃO ENVIADA!!!</b>");
    }
}
