-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema bowlingBD
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `bowlingBD` DEFAULT CHARACTER SET utf8 ;
USE `bowlingBD` ;

-- -------------------------------------------------
-- Table `bowlingBD`.`Cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bowlingBD`.`Cliente` (
  `idCliente` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `apellido` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idCliente`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `bowlingBD`.`Pista`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bowlingBD`.`Pista` (
  `idPista` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idPista`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `bowlingBD`.`Estado`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bowlingBD`.`Estado` (
  `idEstado` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idEstado`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `bowlingBD`.`Reserva`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bowlingBD`.`Reserva` (
  `idReserva` INT NOT NULL AUTO_INCREMENT,
  `Cliente_idCliente` INT NOT NULL,
  `Pista_idPista` INT NOT NULL,
  `fecha` DATE NOT NULL,
  `hora` TIME NOT NULL,
  `Estado_idEstado` INT NOT NULL,
  PRIMARY KEY (`idReserva`),
  INDEX `fk_Reserva_Cliente_idx` (`Cliente_idCliente` ASC),
  INDEX `fk_Reserva_Pista_idx` (`Pista_idPista` ASC),
  INDEX `fk_Reserva_Estado_idx` (`Estado_idEstado` ASC),
  CONSTRAINT `fk_Reserva_Cliente`
    FOREIGN KEY (`Cliente_idCliente`)
    REFERENCES `bowlingBD`.`Cliente` (`idCliente`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Reserva_Pista`
    FOREIGN KEY (`Pista_idPista`)
    REFERENCES `bowlingBD`.`Pista` (`idPista`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Reserva_Estado`
    FOREIGN KEY (`Estado_idEstado`)
    REFERENCES `bowlingBD`.`Estado` (`idEstado`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Insertar datos de ejemplo
-- -----------------------------------------------------
INSERT INTO `bowlingBD`.`Estado` (`nombre`) VALUES
('Activa'),
('Cancelada'),
('Completada');

INSERT INTO `bowlingBD`.`Pista` (`nombre`) VALUES
('Pista 1'),
('Pista 2'),
('Pista 3'),
('Pista 4'),
('Pista 5'),
('Pista 6'),
('Pista 7'),
('Pista 8');

INSERT INTO `bowlingBD`.`Cliente` (`nombre`, `apellido`) VALUES
('Juan', 'Pérez'),
('María', 'González'),
('Luisa', 'Parra'),
('Geiner', 'Devia'),
('Sergio', 'Garcia'),
('Marcela', 'Pineda');

INSERT INTO `bowlingBD`.`Reserva`
(`Cliente_idCliente`, `Pista_idPista`, `fecha`, `hora`, `Estado_idEstado`) VALUES
(1, 1, '2024-03-20', '14:30:00', 1),  -- 20 de marzo

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;