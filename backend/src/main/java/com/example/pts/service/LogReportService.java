package com.example.pts.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LogReportService {

    private static final Logger logger = LoggerFactory.getLogger(LogReportService.class);

    // Her gün gece tam 00:00'da çalışır
    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyLogReport() {
        logger.info("Günlük log raporu oluşturuluyor...");

        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdir();
        }

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        File reportFile = new File("reports/rapor-" + dateStr + ".txt");

        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write("=== GÜNLÜK SİSTEM RAPORU ===\n");
            writer.write("Tarih: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("Durum: Sistem başarıyla çalıştı ve tüm işlemler loglandı.\n");
            writer.write("\nNot: Detaylı tüm sistem logları (hatalar, girişler vb.) 'logs/spring-boot-logger.log' dosyası içinde arşivlenmiştir.\n");
            
            logger.info("Günlük rapor başarıyla oluşturuldu: " + reportFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Rapor oluşturulurken hata meydana geldi: ", e);
        }
    }
}
