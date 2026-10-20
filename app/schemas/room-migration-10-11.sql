-- Room migration 10 -> 11: persistir escolha de término em 100% por teste Labs.
-- Padrão 0 preserva o comportamento de testes legados.
ALTER TABLE `lab_tests` ADD COLUMN `stopAtFull` INTEGER NOT NULL DEFAULT 0;
