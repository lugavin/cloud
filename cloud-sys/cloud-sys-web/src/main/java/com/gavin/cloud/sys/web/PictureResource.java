package com.gavin.cloud.sys.web;

import com.gavin.cloud.common.base.exception.AppException;
import com.gavin.cloud.sys.core.service.PictureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

import static com.gavin.cloud.common.base.exception.DefaultProblemType.INTERNAL_SERVER_ERROR_TYPE;

@RestController
@RequestMapping("/pictures")
public class PictureResource {

    private final PictureService pictureService;

    public PictureResource(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam Part file) {
        try (InputStream local = file.getInputStream()) {
            String imageURL = pictureService.upload(local);
            return ResponseEntity.ok(imageURL);
        } catch (IOException e) {
            throw new AppException(INTERNAL_SERVER_ERROR_TYPE, "Picture upload failed.");
        }
    }

}
