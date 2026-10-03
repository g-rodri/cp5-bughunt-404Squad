package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Testes unitarios do model: sem banco, sem Spring (Aula 15).
public class BanhoTest {

    private Banho banhoDoRex() {
        return new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.of(2026, 10, 1, 10, 0));
    }

    @Test
    public void deveAcumular20PontosDeFidelidade() {
        // Act
        int pontos = banhoDoRex().calcularPontosFidelidade();

        // Assert
        assertEquals(20, pontos);
    }

    @Test
    public void deveCobrarPrecoPorPorteQuandoForBanho() {
        // Arrange: um banho para cada porte
        Banho pequeno = new Banho(1, "Rex", "PEQUENO", "Ana", LocalDateTime.of(2026, 10, 1, 10, 0));
        Banho medio = new Banho(2, "Mimi", "MEDIO", "Bruno", LocalDateTime.of(2026, 10, 1, 11, 0));
        Banho grande = new Banho(3, "Thor", "GRANDE", "Carla", LocalDateTime.of(2026, 10, 1, 12, 0));

        // Act + Assert: tabela de precos do contrato
        assertEquals(60.0, pequeno.calcularPreco(), 0.001);
        assertEquals(80.0, medio.calcularPreco(), 0.001);
        assertEquals(100.0, grande.calcularPreco(), 0.001);
    }
}
