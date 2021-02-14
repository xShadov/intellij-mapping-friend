public class Simple {
    public TestData method() {
        return TestData.builder()<caret>
    }

    @lombok.Value
    @lombok.Builder
    public static class TestData {
        private String one;
        private String two;
    }
}