package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest {
	@Test
    @DisplayName("getNombre devuelve el nombre del combo")
    void nombreDelCombo() {
        ArrayList<ProductoMenu> items = new ArrayList<>();
        items.add(new ProductoMenu("Hamburguesa", 12000));
        items.add(new ProductoMenu("Papas", 7000));

        Combo combo = new Combo("Combo especial", 0.10, items);
        assertEquals("Combo especial", combo.getNombre());
    }

    @Test
    @DisplayName("Precio con 0% de descuento: debe ser la suma de los ítems ")
    void precioSinDescuento() {
        ArrayList<ProductoMenu> items = new ArrayList<>();
        items.add(new ProductoMenu("Hamburguesa", 12000));
        items.add(new ProductoMenu("Papas", 7000));
        items.add(new ProductoMenu("Gaseosa", 5000)); 

        Combo combo = new Combo("Sin desc", 0.0, items);

        int esperado = (int) Math.round(24000 * (1 - 0.0)); 
        assertEquals(esperado, combo.getPrecio(),
                "Con 0% de descuento el precio debe ser la suma de los ítems");
    }

    @Test
    @DisplayName("Precio con 10% de descuento: suma × (1 − 0.10) (fail-first: hoy calculará suma × 0.10)")
    void precioConDescuento10() {
        ArrayList<ProductoMenu> items = new ArrayList<>();
        items.add(new ProductoMenu("Hamburguesa", 12000));
        items.add(new ProductoMenu("Papas", 7000));
        items.add(new ProductoMenu("Gaseosa", 5000)); 

        Combo combo = new Combo("10%", 0.10, items);

        int esperado = (int) Math.round(24000 * (1 - 0.10));
        assertEquals(esperado, combo.getPrecio(),
                "Debe aplicar (1 − descuento) a la suma de los ítems");
    }

    @Test
    @DisplayName("Precio con 100% de descuento: debe ser 0")
    void precioConDescuento100() {
        ArrayList<ProductoMenu> items = new ArrayList<>();
        items.add(new ProductoMenu("Hamburguesa", 12000));
        items.add(new ProductoMenu("Papas", 7000));
        items.add(new ProductoMenu("Gaseosa", 5000)); 

        Combo combo = new Combo("100%", 1.0, items);

        int esperado = (int) Math.round(24000 * (1 - 1.0)); 
        assertEquals(esperado, combo.getPrecio(),
                "Con 100% de descuento el precio debe ser 0");
    }

    @Test
    @DisplayName("Factura: incluye encabezado, línea de descuento y finaliza con total alineado correcto")
    void facturaIncluyeNombreDescuentoYTotalCorrecto() {
        ArrayList<ProductoMenu> items = new ArrayList<>();
        items.add(new ProductoMenu("Hamburguesa", 10000));
        items.add(new ProductoMenu("Papas", 5000)); 

        double descuento = 0.20;
        Combo combo = new Combo("Alineado", descuento, items);

        int totalCorrecto = (int) Math.round(15000 * (1 - descuento)); 
        String sufijoEsperado = "            " + totalCorrecto + "\n"; 

        String factura = combo.generarTextoFactura();
        assertNotNull(factura);

        assertTrue(factura.startsWith("Combo Alineado\n"),
                "La factura debe iniciar con 'Combo <nombre>' y salto de línea");
        assertTrue(factura.contains(" Descuento: " + descuento + "\n"),
                "La factura debe contener la línea de descuento exactamente como se imprime");

        assertTrue(factura.endsWith(sufijoEsperado),
                "La factura debe cerrar con el total correcto y 12 espacios de alineación");
    }

    @Test
    @DisplayName("Factura con 0% descuento: total debe ser la suma; formato exacto de cierre")
    void facturaConCeroPorcientoDescuento() {
        ArrayList<ProductoMenu> items = new ArrayList<>();
        items.add(new ProductoMenu("A", 8000));
        items.add(new ProductoMenu("B", 2000)); 

        double descuento = 0.0;
        Combo combo = new Combo("Cero", descuento, items);

        int totalCorrecto = (int) Math.round(10000 * (1 - descuento)); 
        String sufijoEsperado = "            " + totalCorrecto + "\n";

        String factura = combo.generarTextoFactura();
        assertNotNull(factura);
        assertTrue(factura.startsWith("Combo Cero\n"));
        assertTrue(factura.contains(" Descuento: 0.0\n"));
        assertTrue(factura.endsWith(sufijoEsperado));
    }
}
