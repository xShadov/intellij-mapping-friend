public class Simple {
    public TestData method() {
        return TestData.builder()
                .one() // (optional)
                .two() // (optional)
                .build();
    }

    @lombok.Value
    @lombok.Builder
    public static class TestData {
        private String one;
        private String two;
    }
}