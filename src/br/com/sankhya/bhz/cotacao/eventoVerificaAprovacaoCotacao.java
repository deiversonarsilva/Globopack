package br.com.sankhya.bhz.cotacao;

import br.com.sankhya.bhz.utils.AcessoBanco;
import br.com.sankhya.bhz.utils.ErroUtils;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.sql.ResultSet;

public class eventoVerificaAprovacaoCotacao implements EventoProgramavelJava { // TGFITC
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        BigDecimal numcotacao = vo.asBigDecimalOrZero("NUMCOTACAO");
        BigDecimal codprod = vo.asBigDecimalOrZero("CODPROD");
        String cabecalho = vo.asString("CABECALHO");

        if("S".equals(cabecalho) &&
            persistenceEvent.getModifingFields().isModifing("STATUSPRODCOT"))  {
            String status = vo.asString("STATUSPRODCOT");
            if("A".equals(status)){
                validaliberacao(numcotacao,codprod);
            }

        }
    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }

    public static void validaliberacao(BigDecimal numcotacao, BigDecimal codprod) throws Exception {
        JapeWrapper libDAO = JapeFactory.dao("LiberacaoLimite"); //TSILIB
        DynamicVO libVO = libDAO.findOne("NUCHAVE=? AND TABELA=? AND SEQUENCIA=? AND DHLIB IS NOT NULL", numcotacao,"TGFITC",codprod);
        if(libVO != null){
            return;
        }else {
            try {
                ErroUtils.disparaErro("FAVOR SOLICITAR LIBERAÇÃO");
            } finally {

            }
        }


    }
}
