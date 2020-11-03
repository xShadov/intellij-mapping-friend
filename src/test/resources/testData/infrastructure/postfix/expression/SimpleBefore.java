public class Simple {
    public void method() {
        TestData.builder()<caret>
    }

    @lombok.Value
    @lombok.Builder
    public static class TestData {
        private String one;
        private String two;
    }
}