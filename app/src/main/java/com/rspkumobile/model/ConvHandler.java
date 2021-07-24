package com.rspkumobile.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Created by DK on 10/15/2017.
 */
public class ConvHandler {

    private String messageText;
    private String senderUid;
    private String senderDisplayName;
    private String senderToken;
    private long messageTime;
    private String userAuthority;
    private Map<String,String> quote;
//    private boolean auth;
    public ConvHandler(){}

//    public ChatMessage(String messageText, String displayName, String messageUser) {
//        this.messageText = messageText;
//        this.messageUser = messageUser;
//        this.messageDisplayName=displayName;
//
//        // Initialize to current time
//        messageTime = new Date().getTime();
//    }

//    public ConvHandler(String messageText, String displayName, String messageUid, String userAuthority, String Token, Map<String,String> map){
//        this.messageText = messageText;
//        this.messageDisplayName = displayName;
//        this.messageUid = messageUid;
//        this.userAuthority= userAuthority;
//        this.token=Token;
//        this.citation=map;
//
//        // Initialize to current time
//        messageTime = new Date().getTime();
//    }

    public ConvHandler(String messageText, String displayName, String messageUid, Map<String,String> map){
        this.messageText = messageText;
        this.senderUid = messageUid;
        this.senderDisplayName = displayName;
//        this.userAuthority= "biasa";
//        this.senderToken=senderToken;
        this.quote=map;

        // Initialize to current time
        messageTime = new Date().getTime();
//        this.auth = auth;
    }

    public JSONObject sendNotif(){
        JSONObject params= new JSONObject();
        try {
            params.put("messageText",this.messageText);
            params.put("senderUid",this.senderUid );
            params.put("senderDisplayName",this.senderDisplayName );
            params.put("quote", new JSONObject()
                    .put("uidFrom", this.quote.get("uidFrom"))
                    .put("text", this.quote.get("text")));
            params.put("messageTime", String.valueOf(new Date().getTime()));
            params.put("auth", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String messageUid) {
        this.senderUid = messageUid;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getSenderDisplayName(){return senderDisplayName;}
    public void setSenderDisplayName(String messageTime) {
        this.senderDisplayName = messageTime;
    }

//    public String getUserAuthority(){return userAuthority;}
//    public void setUserAuthority(String userAuthority) {
//        this.userAuthority = userAuthority;
//    }

//    public String getSenderToken(){return senderToken;}
//    public void setSenderToken(String token) {
//        this.senderToken = senderToken;
//    }

    public Map<String,String> getQuote(){return quote;}
    public void setQuote(Map<String,String> map) {
        this.quote = map;
    }

//    public boolean getAuth(){return auth;}
//    public void setAuth(boolean a){this.auth = a;}

}
