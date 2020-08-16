package com.example.matchmusic.Daos

import com.example.matchmusic.Model.Usuario
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UsuarioMDao {
    val coll_name = "users"
    var db = FirebaseFirestore.getInstance()

    fun inserirUsuario(usuario: Usuario) : Task<Void> {
        val task = db
            .collection(coll_name)
            .document(usuario.email)
            .set(usuario)
        return task
    }

    fun atualizarSobreUsuario(usuario: Usuario) : Task<Void> {
        val task = db
            .collection(coll_name)
            .document(usuario.email)
            .update(
                "sobremim", usuario.sobremim,
                "pensamento", usuario.pensamento
            )
        return task
    }

    fun atualizarGeneroUsuario(usuario: Usuario) : Task<Void> {
        val task = db
            .collection(coll_name)
            .document(usuario.email)
            .update("generos", usuario.generos)
        return task
    }

    fun pegarUsuarioPeloEmail(email: String) : Task<DocumentSnapshot> {
        val task = db
            .collection(coll_name)
            .document(email)
            .get()
        return task
    }

    fun pegarUsuarios() : Task<QuerySnapshot> {
        val task = db
            .collection(coll_name)
            .get()
        return task
    }

    fun pegarUsuarioPeloEmailThen(email: String, report: (Boolean, String, DocumentSnapshot?) -> Unit){
        UsuarioMDao().pegarUsuarioPeloEmail(email)
            .addOnFailureListener {
                report(false, it.message.toString(), null)
            }
            .addOnSuccessListener {
                report(true, "Consulta realizada com sucesso", it)
            }
    }

    fun pegarUsuariosThen(report: (Boolean, String, QuerySnapshot?) -> Unit){
        UsuarioMDao().pegarUsuarios()
            .addOnFailureListener {
                report(false, it.message.toString(), null)
            }
            .addOnSuccessListener {
                report(true, "Consulta realizada com sucesso", it)
            }
    }

    fun atualizarSobreUsuarioThen(usuario: Usuario, report: (Boolean, String) -> Unit){
        UsuarioMDao().atualizarSobreUsuario(usuario)
            .addOnFailureListener {
                report(false, it.message.toString())
            }
            .addOnSuccessListener {
                report(true, "Informações do usuario atualizadas [Sobre mim / Pensamento]")
            }
    }

    fun atualizarGeneroUsuarioThen(usuario: Usuario, report: (Boolean, String) -> Unit){
        UsuarioMDao().atualizarGeneroUsuario(usuario)
            .addOnFailureListener {
                report(false, it.message.toString())
            }
            .addOnSuccessListener {
                report(true, "Generos do usuario atualizados com sucesso.")
            }
    }

    fun inserirUsuarioThen(usuario: Usuario, report: (Boolean, String) -> Unit){
        UsuarioMDao().inserirUsuario(usuario)
            .addOnFailureListener {
                report(false, it.message.toString())
            }
            .addOnSuccessListener {
                report(true, "Usuario adicionado com sucesso.")
            }
    }
}