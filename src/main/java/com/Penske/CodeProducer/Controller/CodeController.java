package com.Penske.CodeProducer.Controller;

import com.Penske.CodeProducer.Exception.DuplicateCodeException;
import com.Penske.CodeProducer.Model.CodeEntity;
import com.Penske.CodeProducer.Service.CodeService;
import org.bson.types.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CodeController {

    @Autowired
    CodeService codeService;

    @PostMapping("/sendCode")
    public ResponseEntity<String> sendCode(@RequestBody CodeEntity codeEntity) {
        try {
            return new ResponseEntity<>(codeService.sendCode(codeEntity), HttpStatus.OK);
        } catch (DuplicateCodeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filterData")
    public List<CodeEntity> getFilteredData(@RequestParam("status") String status){
        return codeService.getFilterData(status);
    }


    @GetMapping("/getcodeByLatestVersion")
    public List<CodeEntity> getCodeWithLatestVersion(@RequestParam("status")String status){
        return codeService.getLatestCode(status);
    }

    @GetMapping("/getLatestVersionOfCode")
    public CodeEntity getLatestVersion(@RequestParam String code){
        return codeService.getLatestVersionOfCode(code);
    }
}
