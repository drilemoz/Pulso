-- Room 9 → 10. Médias térmicas legadas continuam desconhecidas (count=0).
ALTER TABLE `lab_tests` ADD COLUMN `temperatureSumC` REAL NOT NULL DEFAULT 0.0;
ALTER TABLE `lab_tests` ADD COLUMN `temperatureCount` INTEGER NOT NULL DEFAULT 0;
ALTER TABLE `lab_tests` ADD COLUMN `electricSource` TEXT NOT NULL DEFAULT '';
