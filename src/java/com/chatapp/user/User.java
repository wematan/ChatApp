/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatapp.user;

/**
 *
 * @author MaTaN
 */
public class User {
   private String id;
   private String name;
   private String email;
   private String phone;
   private String token;
   
   public User(){
   
   }
   
   public User(String id, String name, String email, String phone){
       this.id = id;
       this.name = name;
       this.email = email;
       this.phone = phone;
   }
   public User(String id, String name, String email, String phone, String token){
       this.id = id;
       this.name = name;
       this.email = email;
       this.phone = phone;
       this.token = token;
   }
   
   public void setId(String id){
       this.id = id;
   }
   public void setName(String name){
       this.name = name;
   }
   public void setEmail(String email){
       this.email = email;
   }
   public void setPhone(String phone){
       this.phone = phone;
   }
   public void setToken(String token){
       this.token = token;
   }  
   public String getId(){
       return this.id;
   }
   public String getName(){
       return this.name;
   }
   public String getEmail(){
       return this.email;
   }
   public String getPhone(){
       return this.phone;
   }
   public String getToken(){
       return this.token;
   }
}
