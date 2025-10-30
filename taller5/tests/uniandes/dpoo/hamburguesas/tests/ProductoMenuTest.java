package uniandes.dpoo.hamburguesas.tests;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoMenuTest {
	 @Test
	    @DisplayName("getNombre y getPrecio devuelven los valores asignados en el constructor")
	    void gettersDevuelvenValoresAsignados() {
	        ProductoMenu p = new ProductoMenu("Hamburguesa", 12000);
	        assertEquals("Hamburguesa", p.getNombre());
	        assertEquals(12000, p.getPrecio());
	    }

	    @Test
	    @DisplayName("generarTextoFactura respeta el formato exacto (nombre\\n + 12 espacios + precio + \\n)")
	    void generarTextoFacturaFormatoExacto() {
	        ProductoMenu p = new ProductoMenu("Papas medianas", 7500);

	        String esperado = ""
	                + "Papas medianas\n"
	                + "            7500\n"; 

	        assertEquals(esperado, p.generarTextoFactura());
	    }

	    @Test
	    @DisplayName("Permite precio cero (sin validaciones en el modelo)")
	    void permitePrecioCero() {
	        ProductoMenu p = new ProductoMenu("Agua", 0);
	        assertEquals(0, p.getPrecio());

	        String esperado = ""
	                + "Agua\n"
	                + "            0\n";
	        assertEquals(esperado, p.generarTextoFactura());
	    }
	    
	    
	    
}
