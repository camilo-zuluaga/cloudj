package com.cloudj.backend.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5000", allowedHeaders = "*") // we are using a browser!
public class FileUploadController {

    // handle multipart/form-data
    @PostMapping(path = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String filesUpload(@RequestPart("file") MultipartFile multipartFile,
                              @RequestPart("fileName") String fileName) {
        System.out.println(multipartFile.getOriginalFilename());
        System.out.println(fileName);
        return "";
    }
}
