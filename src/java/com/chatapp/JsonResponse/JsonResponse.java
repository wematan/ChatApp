/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatapp.JsonResponse;

import java.util.ArrayList;

/**
 *
 * @author MaTaN
 */
public class JsonResponse {
   private int status;
   private String message;
   private ArrayList content;
   
   public JsonResponse(){
       
   }
   
   public JsonResponse(int status, String msg, ArrayList list){
       this.status = status;
       this.message = msg;
       this.content = list;
   } 
   
   public void setStatus(int status){
       this.status = status;
   }
   public void setMessage(String msg){
       this.message = msg;
   }
   public void setContent(ArrayList list){
       this.content = list;
   }
   
   public int getStatus(){
       return this.status;
   }
   public String getMessage(){
       return this.message;
   }
   public ArrayList getContent(){
       return this.content;
   }
}

