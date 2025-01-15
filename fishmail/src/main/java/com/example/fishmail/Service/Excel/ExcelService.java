package com.example.fishmail.Service.Excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelService {
    
    public List<String> readRecieviersFromFile(MultipartFile file) throws IOException {
        List<String> recivers = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for(Row row : sheet){
            Cell cell = row.getCell(0); // zakładamy że email jest w pierwszej kolumnie
            if(cell != null){
                recivers.add(cell.getStringCellValue());
            }
        }
        workbook.close();
        return recivers;

    }
}
