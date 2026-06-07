package test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;

/**
 * ===================================================================
 * KIỂM THỬ CẤU TRÚC KIẾN TRÚC (Phần 2/2) — Dành cho UC-03, UC-04
 * ===================================================================
 * Tham chiếu: Chương 8.1.1 - Kiểm tra cấu trúc và kiến trúc mã nguồn
 *
 * Test: Nguyên tắc đơn nhiệm (Single Responsibility)
 *   - Package model/, view/, controller/ phải tồn tại
 *   - ReversiModel.java, ReversiView.java, ReversiController.java đúng vị trí
 */
public class KienTruc_SRP_Test {

    // =====================================================================
    // Kiểm tra nguyên tắc ĐƠN NHIỆM (Single Responsibility Principle)
    // Mỗi package chỉ chứa đúng vai trò của mình:
    //   - model/: Chứa logic dữ liệu (ReversiModel.java)
    //   - view/: Chứa giao diện (ReversiView.java)
    //   - controller/: Chứa điều khiển (ReversiController.java)
    // =====================================================================
    @Test
    public void testCauTrucPackageMVC() {
        assertTrue("Package model/ phải tồn tại", new File("src/model").isDirectory());
        assertTrue("Package view/ phải tồn tại", new File("src/view").isDirectory());
        assertTrue("Package controller/ phải tồn tại", new File("src/controller").isDirectory());
        assertTrue("ReversiModel.java phải nằm trong model/", new File("src/model/ReversiModel.java").exists());
        assertTrue("ReversiView.java phải nằm trong view/", new File("src/view/ReversiView.java").exists());
        assertTrue("ReversiController.java phải nằm trong controller/", new File("src/controller/ReversiController.java").exists());
        System.out.println("  [PASS] 8.1.1: Nguyên tắc đơn nhiệm - Các class phân chia đúng package và trách nhiệm.");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("test.KienTruc_SRP_Test");
    }
}

/*
KẾT QUẢ CHẠY KIỂM THỬ (CONSOLE OUTPUT):
JUnit version 4.13.2
.  [PASS] 8.1.1: Nguyên tắc đơn nhiệm - Các class phân chia đúng package và trách nhiệm.

Time: 0.004

OK (1 test)
*/

