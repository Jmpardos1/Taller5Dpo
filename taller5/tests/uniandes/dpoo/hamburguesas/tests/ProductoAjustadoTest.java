package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest {
	@Test
	@DisplayName("getNombre delega en el producto base")
	void nombreDelegadoAlBase() {
		ProductoMenu base = new ProductoMenu("Hamburguesa sencilla", 12_000);
		ProductoAjustado ajustado = new ProductoAjustado(base);
		assertEquals("Hamburguesa sencilla", ajustado.getNombre());
	}

	@Test
	@DisplayName("Sin modificaciones: getPrecio debe ser igual al precio del base")
	void precioSinModificacionesDebeSerBase() {
		ProductoMenu base = new ProductoMenu("Gaseosa", 3_000);
		ProductoAjustado ajustado = new ProductoAjustado(base);

		assertEquals(3_000, ajustado.getPrecio(),
				"Sin modificaciones, el precio ajustado debe ser el del producto base");
	}

	 @Test
	    @DisplayName("Factura SIN modificaciones: inicia con base.toString() y termina con 12 espacios + getPrecio() + \\n")
	    void facturaSinModificaciones() {
	        ProductoMenu base = new ProductoMenu("Papas medianas", 7_500);
	        ProductoAjustado ajustado = new ProductoAjustado(base);

	        String factura = ajustado.generarTextoFactura();
	        assertNotNull(factura);

	        assertTrue(factura.startsWith(base.toString()));

	        assertFalse(factura.contains("    +"));
	        assertFalse(factura.contains("    -"));

	        String sufijoEsperado = "            " + ajustado.getPrecio() + "\n"; // 12 espacios
	        assertTrue(factura.endsWith(sufijoEsperado));
	    }

	@Test
	@DisplayName("Con agregados y eliminados: total = base + suma(agregados); quitar NO descuenta")
	void precioConAgregados() {
		ProductoMenu base = new ProductoMenu("Hamburguesa sencilla", 12_000);
		Ingrediente queso    = new Ingrediente("Queso", 2_000);
		Ingrediente tocineta = new Ingrediente("Tocineta", 2_500);
		Ingrediente tomate   = new Ingrediente("Tomate", 500);   
		ProductoAjustado ajustado = new ProductoAjustado(base);
		ajustado.agregarIngrediente(queso);
		ajustado.agregarIngrediente(tocineta);
		ajustado.quitarIngrediente(tomate);

		int esperado = 12_000 + 2_000 + 2_500; 
		assertEquals(esperado, ajustado.getPrecio(),
				"El precio ajustado debe ser base + suma de agregados (quitar NO descuenta)");
	}
	@Test
	@DisplayName("Agregados duplicados: cada repetición se cobra")
	void precioConAgregadosDuplicados() {
		ProductoMenu base = new ProductoMenu("Hamburguesa", 10_000);
		Ingrediente queso = new Ingrediente("Queso", 2_000);

		ProductoAjustado ajustado = new ProductoAjustado(base);
		ajustado.agregarIngrediente(queso);
		ajustado.agregarIngrediente(queso); 
		ajustado.agregarIngrediente(new Ingrediente("Tocineta", 2_500));

		int esperado = 10_000 + 2_000 + 2_000 + 2_500; 
		assertEquals(esperado, ajustado.getPrecio(),
				"Cada agregado duplicado debe sumarse nuevamente al total");
	}

	@Test
    @DisplayName("Factura CON agregados y eliminados: '+nombre' luego 16 espacios+costo; '-nombre'; cierra con total")
    void facturaConAgregadosYEliminados() {
        ProductoMenu base = new ProductoMenu("Hamburguesa", 10_000);
        Ingrediente queso1   = new Ingrediente("Queso", 2_000);
        Ingrediente queso2   = new Ingrediente("Queso", 2_000);
        Ingrediente tocineta = new Ingrediente("Tocineta", 2_500);
        Ingrediente cebolla  = new Ingrediente("Cebolla", 300);

        ProductoAjustado ajustado = new ProductoAjustado(base);
        ajustado.agregarIngrediente(queso1);
        ajustado.agregarIngrediente(queso2);
        ajustado.agregarIngrediente(tocineta);
        ajustado.quitarIngrediente(cebolla);

        String factura = ajustado.generarTextoFactura();
        assertNotNull(factura);

        assertTrue(factura.startsWith(base.toString()));

        assertTrue(factura.contains("    +Queso"));
        assertTrue(factura.contains("                2000")); 
        assertTrue(factura.contains("    +Tocineta"));
        assertTrue(factura.contains("                2500"));

        assertTrue(factura.contains("    -Cebolla"));

        String sufijoEsperado = "            " + ajustado.getPrecio() + "\n"; 
        assertTrue(factura.endsWith(sufijoEsperado));
    }
}
