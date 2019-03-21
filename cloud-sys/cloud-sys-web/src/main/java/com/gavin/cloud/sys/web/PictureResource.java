package com.gavin.cloud.sys.web;

import com.gavin.cloud.sys.core.enums.CmsMessageType;
import com.gavin.cloud.sys.core.service.PictureService;
import com.gavin.cloud.common.base.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/pictures")
public class PictureResource {

    @Autowired
    private PictureService pictureService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam Part file) {
        try (InputStream local = file.getInputStream()) {
            String imageURL = pictureService.upload(local);
            return ResponseEntity.ok(imageURL);
        } catch (IOException e) {
            throw new AppException(CmsMessageType.ERR_UPLOAD_PICTURE, "Picture upload failed.", e);
        }
    }

}
