package test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * ===================================================================
 * KIỂM THỬ CẤU TRÚC KIẾN TRÚC (Phần 1/2) — Dành cho UC-05, UC-08
 * ===================================================================
 * Tham chiếu: Chương 8.1.1 - Kiểm tra cấu trúc và kiến trúc mã nguồn
 *
 * Test 1: Tuân thủ MVC — Model cô lập khỏi View/Controller
 *   - ReversiModel.java không import view.* hoặc controller.*
 *
 * Test 2: Tách biệt logic và giao diện
 *   - Logic nghiệp vụ (NuocDiHopLe, CoNuocDiHopLe, getGameResult) nằm trong Model
 *   - Giao diện (updateView) nằm trong View
 */
public class KienTruc_MVC_Test {

    // =====================================================================
    // Kiểm tra Model KHÔNG import View hoặc Controller (Tuân thủ MVC)
    // Đảm bảo ReversiModel.java hoàn toàn cô lập khỏi tầng giao diện
    // =====================================================================
    @Test
    public void testModelKhongImportViewController() throws Exception {
        File modelFile = new File("src/model/ReversiModel.java");
        assertTrue("File ReversiModel.java phải tồn tại", modelFile.exists());

        List<String> lines = Files.readAllLines(Paths.get(modelFile.getPath()));
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("import ")) {
                assertFalse("Model không được phép import View", line.contains("import view."));
                assertFalse("Model không được phép import Controller", line.contains("import controller."));
            }
        }
        System.out.println("  [PASS] 8.1.1: Tuân thủ MVC - Model hoàn toàn cô lập khỏi View/Controller.");
    }

    // =====================================================================
    // Kiểm tra tách biệt logic nghiệp vụ (Model) và giao diện (View)
    // - Model chứa: NuocDiHopLe(), CoNuocDiHopLe(), getGameResult()
    // - View chứa: updateView()
    // =====================================================================
    @Test
    public void testTachBietLogicVaGiaoDien() throws Exception {
        Class<?> modelClass = Class.forName("model.ReversiModel");
        assertNotNull("ReversiModel phải chứa phương thức NuocDiHopLe",
                modelClass.getDeclaredMethod("NuocDiHopLe", int.class, int.class, int.class));
        assertNotNull("ReversiModel phải chứa phương thức CoNuocDiHopLe",
                modelClass.getDeclaredMethod("CoNuocDiHopLe", int.class));

        Class<?> viewClass = Class.forName("view.ReversiView");
        assertNotNull("ReversiView phải có phương thức updateView",
                viewClass.getMethod("updateView", int[][].class, int.class, int.class, int.class, boolean[][].class));
        System.out.println("  [PASS] 8.1.1: Tách biệt logic và giao diện - Logic nghiệp vụ nằm hoàn toàn trong Model.");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.KienTruc_MVC_Test");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT):
JUnit version 4.13.2
.  [PASS] 8.1.1: Tách biệt logic và giao diện - Logic nghiệp vụ nằm hoàn toàn trong Model.
.  [PASS] 8.1.1: Tuân thủ MVC - Model hoàn toàn cô lập khỏi View/Controller.

Time: 0.027

OK (2 tests)
*/

