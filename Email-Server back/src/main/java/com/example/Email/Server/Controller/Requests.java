package com.example.Email.Server.Controller;

import com.example.Email.Server.model.Ibulider1;
import com.example.Email.Server.model.User;
import com.example.Email.Server.model.validation;
import com.google.gson.Gson;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.json.JSONObject;

import java.util.HashMap;

@RestController
@EnableWebMvc
@CrossOrigin

public class Requests {
    MainController controller = new MainController() ;
     User user =new User();

    @PostMapping ("/signup")
    public String SignupRequest(@RequestBody String email) throws JSONException {

        return new Gson().toJson(controller.Signup(email))  ;

    }
    @GetMapping ("/login")
    public String loginRequest(@RequestParam String email, @RequestParam String password)
    {

        return  controller.login(email, password);
    }

    @PostMapping("/sendEmail")
    public String sendRequest(@RequestBody String mail)
    {
        return new Gson().toJson(controller.sendEmail(mail))  ;
    }
    @PostMapping ("/addcontact")
    public String addcontact(@RequestBody String addcontact)
    {
        return new Gson().toJson(controller.addcontact(addcontact))  ;
    }
    @GetMapping ("/editcontact")
    public String editcontact(@RequestParam String contact, @RequestParam String email)
    {
        return controller.editcontact(contact, email) ;
    }
    @GetMapping("/deletecontact")
    public String deletecontact(@RequestParam String contact, @RequestParam String email)
    {
        return controller.deletecontact(contact, email) ;
    }


    //for files

    @PostMapping("/addfolder")
    public String addFolder(@RequestBody String addFolder) throws JSONException {
        return new Gson().toJson(controller.addFolder(addFolder))  ;
    }

    @PostMapping("/deletefolder")
    public String deletefolder(@RequestBody String deletefolder){
        return new Gson().toJson(controller.deleteFolder(deletefolder)) ;
    }

    @PostMapping("/movemailtofolder")
    public String move(@RequestBody String move){
        return new Gson().toJson(controller.moveFromFolderToFolder(move)) ;
    }

    @GetMapping("/renamefolder")
    public String renameFolder(@RequestParam String email,@RequestParam String oldname,@RequestParam String newname){
        return controller.renameFolder(email,oldname,newname);
    }

}
