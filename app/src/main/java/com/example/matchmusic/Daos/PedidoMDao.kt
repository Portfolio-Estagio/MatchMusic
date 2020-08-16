package com.example.matchmusic.Daos

import com.example.matchmusic.Model.Amizade
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PedidoMDao {
    val coll_name = "pedidos"
    var db = FirebaseFirestore.getInstance()

    private fun deletarPedido(idDocumento: String): Task<Void> {
        val task = db
            .collection(coll_name)
            .document(idDocumento)
            .delete()
        return task
    }

    private fun pegarPedidos(): Task<QuerySnapshot> {
        val task = db
            .collection(coll_name)
            .get()
        return task
    }

    private fun pegarPedidoPorEmails(solicitante: String, solicitado: String): Task<QuerySnapshot> {
        val task = db
            .collection(coll_name)
            .whereEqualTo(
                "solicitado", solicitado
            ).whereEqualTo(
                "solicitante",
                solicitante
            ).get()
        return task
    }

    private fun inserirPedido(amizade: Amizade) : Task<Void> {
        val task = db
            .collection(coll_name)
            .document()
            .set(amizade)
        return task
    }

    private fun atualizarPedido(idDocumento: String, amizade: Amizade): Task<Void> {
        val task = db
            .collection(coll_name)
            .document(idDocumento)
            .update("confirmacao", amizade.confirmacao)
        return task
    }

    fun deletarPedidoThen(idDocumento: String, report: (Boolean, String) -> Unit){
        PedidoMDao().deletarPedido(idDocumento)
            .addOnFailureListener {
                report(false, it.message.toString())
            }
            .addOnSuccessListener {
                report(true, "Pedido recusado com sucesso.")
            }
    }

    fun inserirPedidoThen(amizade: Amizade, report: (Boolean, String) -> Unit){
        PedidoMDao().inserirPedido(amizade)
            .addOnFailureListener {
                report(false, it.message.toString())
            }
            .addOnSuccessListener {
                report(true, "Inserção de amizade feita com sucesso.")
            }
    }

    fun pegarPedidosThen(report: (Boolean, String, QuerySnapshot?) -> Unit){
        PedidoMDao().pegarPedidos()
            .addOnFailureListener {
                report(false, it.message.toString(), null)
            }
            .addOnSuccessListener {
                report(true, "Consulta realizada com sucesso", it)
            }
    }

    fun pegarPedidoPorEmailsThen(solicitante: String, solicitado: String, report: (Boolean, String, QuerySnapshot?) -> Unit){
        PedidoMDao().pegarPedidoPorEmails(solicitante, solicitado)
            .addOnFailureListener {
                report(false, it.message.toString(), null)
            }
            .addOnSuccessListener {
                report(true, "Consulta realizada com sucesso", it)
            }
    }

    fun atualizarPedidoThen(idDocumento: String, amizade: Amizade, report: (Boolean, String) -> Unit){
        PedidoMDao().atualizarPedido(idDocumento, amizade)
            .addOnFailureListener {
                report(false, it.message.toString())
            }
            .addOnSuccessListener {
                report(true, "Pedido atualizado com sucesso")
            }
    }
}