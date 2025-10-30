package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoTest {
	 @Test
	    @DisplayName("Constructor asigna cliente/dirección y los IDs son incrementales")
	    void constructorEIdsIncrementales() {
	        Pedido p1 = new Pedido("Alice", "Av 1");
	        Pedido p2 = new Pedido("Bob", "Av 2");
	        assertEquals("Alice", p1.getNombreCliente());
	        assertEquals(p1.getIdPedido() + 1, p2.getIdPedido());
	    }

	    @Test
	    @DisplayName("generarTextoFactura: pedido vacío muestra encabezados y Neto=0, IVA=0, Total=0 (IVA truncado)")
	    void facturaPedidoVacio() {
	        Pedido pedido = new Pedido("Carol", "Calle 99");

	        String factura = pedido.generarTextoFactura();
	        assertNotNull(factura);

	        assertTrue(factura.contains("Cliente: Carol"));
	        assertTrue(factura.contains("Dirección: Calle 99"));
	        assertTrue(factura.contains("----------------\n"));
	        assertTrue(factura.contains("Precio Neto:  0"));
	        assertTrue(factura.contains("IVA:          0"));
	        assertTrue(factura.contains("Precio Total: 0"));
	    }

	    @Test
	    @DisplayName("generarTextoFactura: con productos suma Neto, aplica IVA 19% truncado y Total = Neto + IVA")
	    void facturaConProductos_IVATruncado() {
	        Pedido pedido = new Pedido("Dave", "Cra 7");

	        ProductoMenu hamburguesa = new ProductoMenu("Hamburguesa", 12000);
	        ProductoMenu papas       = new ProductoMenu("Papas", 7000);
	        pedido.agregarProducto(hamburguesa);
	        pedido.agregarProducto(papas);

	        int netoEsperado  = 12000 + 7000;         
	        int ivaEsperado   = (int)(netoEsperado * 0.19); 
	        int totalEsperado = netoEsperado + ivaEsperado; 

	        String factura = pedido.generarTextoFactura();
	        assertNotNull(factura);

	        assertTrue(factura.contains("Hamburguesa"));
	        assertTrue(factura.contains("Papas"));

	        assertTrue(factura.contains("Precio Neto:  " + netoEsperado));
	        assertTrue(factura.contains("IVA:          " + ivaEsperado));
	        assertTrue(factura.contains("Precio Total: " + totalEsperado));
	    }

	    @Test
	    @DisplayName("getPrecioTotalPedido = Neto + IVA (IVA 19% truncado)")
	    void totalEsNetoMasIva_Truncado() {
	        Pedido pedido = new Pedido("Eve", "Diag 10");
	        pedido.agregarProducto(new ProductoMenu("A", 10000));
	        pedido.agregarProducto(new ProductoMenu("B", 5000)); 

	        int neto = 15000;
	        int iva  = (int)(neto * 0.19); 
	        int total = neto + iva;        

	        assertEquals(total, pedido.getPrecioTotalPedido());
	    }

	    @Test
	    @DisplayName("guardarFactura: crea el archivo y su contenido coincide exactamente con generarTextoFactura")
	    void guardarFacturaCreaArchivoYContenido(@TempDir Path temp) throws IOException, FileNotFoundException {
	        Pedido pedido = new Pedido("Frank", "Circular 77");
	        pedido.agregarProducto(new ProductoMenu("Hamburguesa", 12000));
	        pedido.agregarProducto(new ProductoMenu("Papas", 7000));

	        File destino = temp.resolve("factura.txt").toFile();
	        String esperado = pedido.generarTextoFactura();

	        pedido.guardarFactura(destino);

	        assertTrue(destino.exists(), "Debe crearse el archivo de factura");
	        String contenido = Files.readString(destino.toPath());
	        assertEquals(esperado, contenido, "El archivo debe contener exactamente la factura generada");
	    }

	    @Test
	    @DisplayName("guardarFactura sobre un directorio lanza FileNotFoundException")
	    void guardarFacturaContraDirectorio(@TempDir Path temp) {
	        Pedido pedido = new Pedido("Gina", "Transv 12");
	        pedido.agregarProducto(new ProductoMenu("A", 1000));

	        File directorio = temp.toFile();
	        assertThrows(FileNotFoundException.class, () -> pedido.guardarFactura(directorio));
	    }
}
