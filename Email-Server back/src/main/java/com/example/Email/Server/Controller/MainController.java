package com.example.Email.Server.Controller;

import com.example.Email.Server.Filter.FilterController;
import com.example.Email.Server.model.*;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;
import org.json.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class MainController {
    Email mails = Email.getInstance();
    director Direct = new director();
    sorting sortt =new sorting();
    FilterController filter = new FilterController() ;

    Message tempOfLastMessages;

    public String Signup(String email) throws JSONException {
        JSONObject json = new JSONObject(email);
        String Email  =  json.getString("email");
        String password  =  json.getString("password");
        System.out.println(Email);
        boolean result;
        validation valid =new validation();
       result= valid.isValid(Email);
       System.out.println(result);
        if(result==true)
        {
            User user = new User();
            user = Direct.construct(user , Email, password) ;
            mails.add(Email, user);

        }
        else
        {
            return "n valid form for email";
        }

        update() ;

        return "user is added";
    }


    public String login(String email, String password)
    {
        boolean result;

        result= mails.existUser(email,password);

        System.out.println("in login");

        mails.print();

        if(result==true)
        {
            return mails.getUser(email).gerUserData() ;
        }
        else
        {
            return "invalid ";
        }
    }

    public String sendEmail (String mailStr){
        System.out.println(mailStr);

        Message m = new Message(mailStr);

        // check with json scheme

        String[] toArr= m.getTo() ;
        if(toArr == null){
            return "to is empty" ;
        }
        for(int i=0 ; i< toArr.length ; i++){
            if (! mails.existemail(toArr[i])){
                return (toArr[i]+ " do not exist") ;
            }else if(toArr[i].equals(m.getFrom())){
                return "Can not send mail to yourself" ;
            }
        }

        User from = mails.getUser(m.getFrom()) ;
        from.addsend(m);
        tempOfLastMessages = m ;
        System.out.println("add from");

        for(int i=0 ; i< toArr.length ; i++){
            User to = mails.getUser(toArr[i]) ;
            Message copy = m.deepCopy() ;
            System.out.println("add to");

            to.addinbox(copy);
        }
        update() ;

        return "done" ;

        // save mails
    }

    public String sendFiles(MultipartFile[] files){
        try {
            tempOfLastMessages.setAttachments(files);
            System.out.println("Done writing file");
            return "done" ;
        }catch (Exception e){
            System.out.println("error writing file");

            return "Error" ;
        }

    }

    public Path getfiles(String fileName) {
        Path res = Paths.get("files/").toAbsolutePath().normalize().resolve(fileName) ;
        if(Files.exists(res)){
            System.out.println("will send");
            return res ;
        }
        System.out.println("will NOT send");
        return null ;

    }

    public String[] filesNames(String email, String ID){
        Message m = mails.getUser(email).inbox.getMessage(Long.parseLong(ID));
        return m.getAttachNames();

    }

    public String draftEmail (String mailStr){
        System.out.println(mailStr);

        Message m = new Message(mailStr);

        User from = mails.getUser(m.getFrom()) ;
        from.adddraft(m);
        // save mails
        update() ;

        return "done" ;

    }

    public String deleteEmail(String user, String folder, String[] ID){
        User theUser = mails.getUser(user) ;
        try {
            for(String id : ID){
                theUser.deleteMessage(Long.parseLong(id), folder);
            }
            update() ;
            return "done" ;
        }catch (Exception e){
            return "Error" ;
        }

    }

    public String addcontact(String addcontact)
    {

        String user ;
        String name ;
        String emails ;
        try {
            JSONObject jas = new JSONObject(addcontact) ;
            user = jas.getString("user");

            name = jas.getString("name") ;
            emails = jas.getString("emails") ;
            System.out.println(user);
            String contact = new JSONObject().put(name, emails).toString() ;

            User theUser = mails.getUser(user) ;
            if (theUser.addconctact(contact)){
                update() ;
                return "contact added";
            }
            else {
                return "contact do not added";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "contact do not added";

        }

    }
    public String editcontact(String user, String oldname, String newname, String[] emails)
    {
        User theUser = mails.getUser(user) ;
        if(theUser.editcontact(oldname, newname, emails)){
            update() ;
            return "contact edited";
        }else
            return "contact do not edited" ;
    }
    public String deletecontact(String user, String name )
    {
        User theUser = mails.getUser(user) ;
        theUser.deletecontact(name);

        update() ;
        return "contact deleted";
    }

    private void update(){
        mails.save();
    }


    // File requests


    public String addFolder(String addFolder) {
        try {
            JSONObject jas = new JSONObject(addFolder) ;
            String email = jas.getString("email");
            String name = jas.getString("name") ;

            User user = mails.getUser(email) ;
            user.addFolder(name);
            System.out.println("Done");
            update() ;
            return "Done" ;
        } catch (JSONException e) {
            e.printStackTrace();
            return "Can not add contact" ;
        }
    }

    public String deleteFolder(String email, String name){
        User user = mails.getUser(email) ;
        user.deleteFolder(name);
        System.out.println("Done");
        update() ;
        return "Done" ;

    }

    public String moveFromFolderToFolder(String move){

        try {
            JSONObject jas = new JSONObject(move) ;
            String email = jas.getString("email");
            String firstFolder = jas.getString("from");
            String secondFolder = jas.getString("to") ;
            JSONArray IDs = jas.getJSONArray("IDs") ;


            for(int i=0 ; i< IDs.length() ; i++){
                long ID = IDs.getLong(i);
                User user = mails.getUser(email) ;
                user.moveFromFolderToFolder(ID, firstFolder,secondFolder);
                System.out.println("Done");
                update() ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Can not move contact" ;
        }
        return "Done" ;
    }

    public String renameFolder(String email, String oldname, String newname){
        try {
            User user = mails.getUser(email) ;
            user.renameFolder(oldname,newname);
            update() ;
            return "Done" ;
        }catch (Exception e){
            return "Can not rename folder" ;
        }

    }
   public int[] getarraysorted( String body, String foldr, String method)
   {
       return sortt.sorted(body,foldr,method);
   }
   public LinkedList contactsorted(String body)
   {
       sorting sor = new sorting();
     return sor.SORTCONTACT(body);
   }

   public String searchEmails(String user, String folder, String searchBy, String equal){
        User u = mails.getUser(user);
        LinkedList<String> res = filter.search(u, folder, searchBy, equal);
        return new Gson().toJson(res)  ;
   }

   public String filterToFolder(String user, String folder, String searchBy, String equal, String name){
       User u = mails.getUser(user);
       LinkedList res = filter.filterToFolder(u, folder, searchBy, equal, name);
       update();
       return new Gson().toJson(res)  ;
   }

    public String searchContacts(String user, String searchEqual){
        User u = mails.getUser(user);
        LinkedList res = filter.searchContacts(u, searchEqual);

        return new Gson().toJson(res)  ;

    }


}
