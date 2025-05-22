package com.vuelosapp.flightreservation.controller;

import com.vuelosapp.flightreservation.entity.User;
import com.vuelosapp.flightreservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

}
