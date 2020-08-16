package com.example.matchmusic.Model;

import com.example.matchmusic.throwss.*;
import java.util.ArrayList;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private String pensamento;
    private String sobremim;
    private ArrayList<String> generos;
    private ArrayList<Usuario> amigos;

    public String getNome() {
        return nome;
    }
    public Usuario(){

    }

    public Usuario(String nome, String sobrenome, String email, String senha, String pensamento, String sobremim, ArrayList<String> generos, ArrayList<Usuario> amigos){
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.senha = senha;
        this.pensamento = pensamento;
        this.sobremim = sobremim;
        this.generos = generos;
        this.amigos = amigos;
    }

    public void setNome(String nome) throws UsuarioNomeException {
        if(nome.length() < 3) {
            throw new UsuarioNomeException();
        } else {
            this.nome = nome;
        }
    }

    public String nomeCompleto(){
        String nomeCompleto = String.format("%s %s", nome, sobrenome);
        return nomeCompleto.toUpperCase();
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
            this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPensamento() {
        return pensamento;
    }

    public void setPensamento(String pensamento) {
        this.pensamento = pensamento;
    }

    public String getSobremim() {
        return sobremim;
    }

    public void setSobremim(String sobremim) {
        this.sobremim = sobremim;
    }

    public ArrayList<String> getGeneros() {
        return generos;
    }

    public void setGeneros(ArrayList<String> generos) {
        this.generos = generos;
    }

    public ArrayList<Usuario> getAmigos(){
        return amigos;
    }

    public void setAmigos(ArrayList<Usuario> amigos) {
        this.amigos = amigos;
    }
}
