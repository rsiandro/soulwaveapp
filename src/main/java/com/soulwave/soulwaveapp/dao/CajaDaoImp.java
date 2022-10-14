package com.soulwave.soulwaveapp.dao;
import com.soulwave.soulwaveapp.models.Movimiento;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class CajaDaoImp implements CajaDao {

    @PersistenceContext
    EntityManager entityManagerMov;

    @Transactional
    public List<Movimiento> getMovimientos(String fechainicio, String fechafin) {

        String query1 = "FROM Movimiento WHERE fechayhora between '";
        String query2 = "' AND '";
        String query3 = "'";
        String query = query1+fechainicio+" 00:00"+query2+fechafin+" 23:59"+query3;

        return entityManagerMov.createQuery(query).getResultList();

    }

    @PersistenceContext
    EntityManager entityManagerBal;
    @Transactional
    public List<Movimiento> getMovimientosBal(String fechainiciobal, String fechafinbal, boolean inEfectivo,
                                              boolean inTarjeta, boolean inTransferencia, boolean inMercadoPago,
                                              boolean egEfectivo, boolean egCheques, boolean egTransferencia,
                                              boolean egIVA, boolean egDepositos) {

        //*************** Formación de la query a partir de los datos capturados ***************

        // Utilización de las variables de fecha para formar la primera parte de la query

        String queryFecha1 = "FROM Movimiento WHERE (fechayhora between '";
        String queryFecha2 = "' AND '";
        String queryFecha3 = "')";

        String queryFecha = queryFecha1+fechainiciobal+" 00:00"+queryFecha2+fechafinbal+" 23:59"+queryFecha3; // Variable string para la primera parte de la query

        // Utilización de las variables booleanas para formar la segunda parte de la query

            // Creación de arreglos con las variables booleanas para luego contabilizar los valores de tipo "ingreso" y "egreso"
        boolean[] inArray = new boolean[4];
        inArray[0] = inEfectivo;
        inArray[1] = inTarjeta;
        inArray[2] = inTransferencia;
        inArray[3] = inMercadoPago;
        int inCont = 0;

        for (int i = 0; i < inArray.length; i++){
            if (inArray[i]){
                inCont ++;
            }
        }

        boolean[] egArray = new boolean[5];
        egArray[0] = egEfectivo;
        egArray[1] = egCheques;
        egArray[2] = egTransferencia;
        egArray[3] = egIVA;
        egArray[4] = egDepositos;
        int egCont = 0;

        for (int j = 0; j < egArray.length; j++){
            if (egArray[j]){
                egCont ++;
            }
        }

        int contTrue = inCont + egCont; // variable entera que suma la cantidad total de variables booleanas con valor true.

            // Formación del String según lo contabilizado de cada array
        String queryTipo = ""; // Variable string para la segunda parte de la query

        if ((inCont >= 1) && (egCont >= 1)){
            queryTipo = "(tipo = 'ingreso' or tipo = 'egreso')";
        } else if (inCont >= 1){
            queryTipo = "(tipo = 'ingreso')";
        } else if (egCont >= 1){
            queryTipo = "(tipo = 'egreso')";
        }

        // Utilización de condicionales para formar la tercera parte de la query

            // Declaración de strings auxiliares
        String queryMedio = ""; // Variable string para la tercera parte de la query
        String inEfectivoMedio, inTarjetaMedio, inTransferenciaMedio, inMPMedio, egEfectivoMedio,
                egChequesMedio, egTransferenciaMedio, egIVAMedio, egDepositosMedio;

        if (inEfectivo) {
            inEfectivoMedio = "id_med = '1'";
                queryMedio = inEfectivoMedio;
        }
        if (inTarjeta) {
            inTarjetaMedio = "id_med = '2'";
            if (contTrue >= 2) {
                queryMedio = queryMedio + " OR " + inTarjetaMedio;
            } else {
                queryMedio = inTarjetaMedio;
            }
        }
        if (inTransferencia) {
            inTransferenciaMedio = "id_med = '4'";
            if (contTrue >= 2) {
                queryMedio = queryMedio + " OR " + inTransferenciaMedio;
            } else {
                queryMedio = inTransferenciaMedio;
            }
        }
        if (inMercadoPago){
            inMPMedio = "id_med = '6'";
            if (contTrue >= 2) {
                queryMedio = queryMedio + " OR " + inMPMedio;
            } else {
                queryMedio = inMPMedio;
            }
        }
        if (egEfectivo){
            egEfectivoMedio = "id_med = '1'";
            if (contTrue >= 2) {
                queryMedio = queryMedio + " OR " + egEfectivoMedio;
            } else {
                queryMedio = egEfectivoMedio;
            }
        }
        if (egCheques){
            egChequesMedio = "id_med = '7'";
            if (contTrue >= 2) {
                queryMedio = queryMedio + " OR " + egChequesMedio;
            } else {
                queryMedio = egChequesMedio;
            }
        }
        if (egTransferencia){
            egTransferenciaMedio = "id_med = '4'";
            if (contTrue >= 2) {
                queryMedio = queryMedio + " OR " + egTransferenciaMedio;
            } else {
                queryMedio = egTransferenciaMedio;
            }
        }
        if (egIVA){
            egIVAMedio = "id_med = '8'";
            if (contTrue >= 2) {
                queryMedio = queryMedio + " OR " + egIVAMedio;
            } else {
                queryMedio = egIVAMedio;
            }
        }
        if (egDepositos){
            egDepositosMedio = "id_med = '9'";
            if (contTrue >= 2) {
                queryMedio = queryMedio + " OR " + egDepositosMedio;
            } else {
                queryMedio = egDepositosMedio;
            }
        }

        queryMedio = "(" + queryMedio + ")";

        // Armado final de la query
        String and = "";

        if (contTrue >= 1) {
            and = " AND ";
        }

        String query = queryFecha + and + queryTipo + and + queryMedio;

        return entityManagerBal.createQuery(query).getResultList();

    }
}
