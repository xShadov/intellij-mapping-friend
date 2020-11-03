public class Simple {
    public void method() {
        TestData.builder()
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