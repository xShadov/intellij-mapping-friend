public class Simple {
    public void method() {
        TestData da<caret>ta;
    }

    @lombok.Value
    @lombok.Builder
    public static class TestData {
        private String one;
        private String two;
    }
}