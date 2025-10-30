package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

public class RestauranteTest {
	@Test
	@DisplayName("Constructor deja listas vacías y sin pedido en curso")
	void constructorInicialVacio() {
		Restaurante r = new Restaurante();
		assertNotNull(r.getIngredientes());
		assertNotNull(r.getMenuBase());
		assertNotNull(r.getMenuCombos());
		assertNotNull(r.getPedidos());
		assertTrue(r.getIngredientes().isEmpty());
		assertTrue(r.getMenuBase().isEmpty());
		assertTrue(r.getMenuCombos().isEmpty());
		assertTrue(r.getPedidos().isEmpty());
		assertNull(r.getPedidoEnCurso());
	}

	@Test
	@DisplayName("iniciarPedido: crea pedido en curso con datos del cliente")
	void iniciarPedidoCreaPedidoEnCurso() throws YaHayUnPedidoEnCursoException {
		Restaurante r = new Restaurante();
		r.iniciarPedido("Alice", "Av 1");
		assertNotNull(r.getPedidoEnCurso());
		assertEquals("Alice", r.getPedidoEnCurso().getNombreCliente());
	}

	@Test
	@DisplayName("iniciarPedido: si ya hay uno, lanza YaHayUnPedidoEnCursoException")
	void iniciarPedidoConUnoExistente() throws YaHayUnPedidoEnCursoException {
		Restaurante r = new Restaurante();
		r.iniciarPedido("Alice", "Av 1");
		assertThrows(YaHayUnPedidoEnCursoException.class,
				() -> r.iniciarPedido("Bob", "Av 2"));
	}

	@Test
	@DisplayName("cerrarYGuardarPedido: si no hay pedido en curso lanza NoHayPedidoEnCursoException")
	void cerrarSinPedidoEnCursoLanza() {
		Restaurante r = new Restaurante();
		assertThrows(NoHayPedidoEnCursoException.class, r::cerrarYGuardarPedido);
	}

	@Test
	@DisplayName("cerrarYGuardarPedido: escribe ./facturas/factura_<id>.txt, limpia el pedido en curso y (especificación) agrega al histórico")
	void cerrarYGuardarPedidoFlujo() throws Exception {
		File facturasDir = new File("./facturas/");
		if (!facturasDir.exists()) assertTrue(facturasDir.mkdirs(), "No se pudo crear ./facturas/");

		Restaurante r = new Restaurante();
		r.iniciarPedido("Carol", "Calle 99");
		r.getPedidoEnCurso().agregarProducto(new ProductoMenu("Hamburguesa", 12000));
		r.getPedidoEnCurso().agregarProducto(new ProductoMenu("Papas", 7000));

		int id = r.getPedidoEnCurso().getIdPedido();

		assertTrue(r.getPedidos().isEmpty());

		r.cerrarYGuardarPedido();

		assertNull(r.getPedidoEnCurso(), "Después de cerrar, no debe haber pedido en curso");

		File esperado = new File("./facturas/factura_" + id + ".txt");
		assertTrue(esperado.exists(), "Debe existir el archivo de factura con el id del pedido");

		assertFalse(r.getPedidos().isEmpty(),
				"Después de cerrar, el pedido debería estar en el histórico (bug si sigue vacío)");
	}

	@Test
	@DisplayName("cargarInformacionRestaurante: carga ingredientes, menú y combos en un caso válido")
	void cargarInformacionCasoValido(@TempDir Path temp) throws Exception {
		File fIng = temp.resolve("ingredientes.txt").toFile();
		try (FileWriter w = new FileWriter(fIng)) {
			w.write("Tomate;500\n");
			w.write("Queso;1000\n");
		}

		File fMenu = temp.resolve("menu.txt").toFile();
		try (FileWriter w = new FileWriter(fMenu)) {
			w.write("Hamburguesa;12000\n");
			w.write("Papas;7000\n");
			w.write("Gaseosa;5000\n");
		}

		File fCombos = temp.resolve("combos.txt").toFile();
		try (FileWriter w = new FileWriter(fCombos)) {
			w.write("Pareja;10%;Hamburguesa;Papas\n");
			w.write("Refresco;20%;Gaseosa\n");
		}

		Restaurante r = new Restaurante();
		r.cargarInformacionRestaurante(fIng, fMenu, fCombos);

		assertEquals(2, r.getIngredientes().size());
		assertEquals(3, r.getMenuBase().size());
		assertEquals(2, r.getMenuCombos().size());

		boolean tieneHamb = r.getMenuBase().stream().anyMatch(p -> p.getNombre().equals("Hamburguesa"));
		boolean tienePapas = r.getMenuBase().stream().anyMatch(p -> p.getNombre().equals("Papas"));
		boolean tieneGaseosa = r.getMenuBase().stream().anyMatch(p -> p.getNombre().equals("Gaseosa"));
		assertTrue(tieneHamb && tienePapas && tieneGaseosa);

		boolean tienePareja = r.getMenuCombos().stream().anyMatch(c -> c.getNombre().equals("Pareja"));
		boolean tieneRefresco = r.getMenuCombos().stream().anyMatch(c -> c.getNombre().equals("Refresco"));
		assertTrue(tienePareja && tieneRefresco);
	}

	@Test
	@DisplayName("cargarIngredientes: ingrediente repetido lanza IngredienteRepetidoException")
	void cargarIngredientesRepetidos(@TempDir Path temp) throws Exception {
		File fIng = temp.resolve("ingredientes.txt").toFile();
		try (FileWriter w = new FileWriter(fIng)) {
			w.write("Tomate;500\n");
			w.write("Tomate;600\n"); 
		}
		File fMenu = temp.resolve("menu.txt").toFile();
		try (FileWriter w = new FileWriter(fMenu)) { }
		File fCombos = temp.resolve("combos.txt").toFile();
		try (FileWriter w = new FileWriter(fCombos)) {}

		Restaurante r = new Restaurante();
		assertThrows(IngredienteRepetidoException.class,
				() -> r.cargarInformacionRestaurante(fIng, fMenu, fCombos));
	}

	@Test
	@DisplayName("cargarMenu: producto repetido lanza ProductoRepetidoException")
	void cargarMenuRepetidos(@TempDir Path temp) throws Exception {
		File fIng = temp.resolve("ingredientes.txt").toFile();
		try (FileWriter w = new FileWriter(fIng)) {
			w.write("Tomate;500\n");
		}
		File fMenu = temp.resolve("menu.txt").toFile();
		try (FileWriter w = new FileWriter(fMenu)) {
			w.write("Hamburguesa;12000\n");
			w.write("Hamburguesa;15000\n"); // repetido
		}
		File fCombos = temp.resolve("combos.txt").toFile();
		try (FileWriter w = new FileWriter(fCombos)) { /* vacío */ }

		Restaurante r = new Restaurante();
		assertThrows(ProductoRepetidoException.class,
				() -> r.cargarInformacionRestaurante(fIng, fMenu, fCombos));
	}

	@Test
	@DisplayName("cargarCombos: combo repetido lanza ProductoRepetidoException")
	void cargarCombosRepetidos(@TempDir Path temp) throws Exception {
		File fIng = temp.resolve("ingredientes.txt").toFile();
		try (FileWriter w = new FileWriter(fIng)) {
			w.write("Tomate;500\n");
		}
		File fMenu = temp.resolve("menu.txt").toFile();
		try (FileWriter w = new FileWriter(fMenu)) {
			w.write("Hamburguesa;12000\n");
			w.write("Papas;7000\n");
		}
		File fCombos = temp.resolve("combos.txt").toFile();
		try (FileWriter w = new FileWriter(fCombos)) {
			w.write("Pareja;10%;Hamburguesa;Papas\n");
			w.write("Pareja;15%;Hamburguesa\n"); 
		}

		Restaurante r = new Restaurante();
		assertThrows(ProductoRepetidoException.class,
				() -> r.cargarInformacionRestaurante(fIng, fMenu, fCombos));
	}

	@Test
	@DisplayName("cargarCombos: producto faltante en un combo lanza ProductoFaltanteException")
	void cargarCombosProductoFaltante(@TempDir Path temp) throws Exception {
		File fIng = temp.resolve("ingredientes.txt").toFile();
		try (FileWriter w = new FileWriter(fIng)) {
			w.write("Tomate;500\n");
		}
		File fMenu = temp.resolve("menu.txt").toFile();
		try (FileWriter w = new FileWriter(fMenu)) {
			w.write("Hamburguesa;12000\n");
		}
		File fCombos = temp.resolve("combos.txt").toFile();
		try (FileWriter w = new FileWriter(fCombos)) {
			w.write("Pareja;10%;Hamburguesa;Papas\n"); 
		}

		Restaurante r = new Restaurante();
		assertThrows(ProductoFaltanteException.class,
				() -> r.cargarInformacionRestaurante(fIng, fMenu, fCombos));
	}

	@Test
	@DisplayName("cargarInformacionRestaurante: burbujea NumberFormatException cuando hay datos no numéricos")
	void cargarInformacionDatosInvalidos(@TempDir Path temp) throws Exception {
		File fIng = temp.resolve("ingredientes.txt").toFile();
		try (FileWriter w = new FileWriter(fIng)) {
			w.write("Tomate;abc\n"); 
		}
		File fMenu = temp.resolve("menu.txt").toFile();
		try (FileWriter w = new FileWriter(fMenu)) {}
		File fCombos = temp.resolve("combos.txt").toFile();
		try (FileWriter w = new FileWriter(fCombos)) {}

		Restaurante r = new Restaurante();
		assertThrows(NumberFormatException.class,
				() -> r.cargarInformacionRestaurante(fIng, fMenu, fCombos));
	}
}
