-- Referência SQL da MIGRATION_8_9. Registros antigos ficam NULL (pico histórico não comprovado).
ALTER TABLE `sessions` ADD COLUMN `maxTemperatureC` REAL;
