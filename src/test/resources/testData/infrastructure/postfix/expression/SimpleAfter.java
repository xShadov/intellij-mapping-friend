public class Simple {
    public void method() {
        TestData.builder()
                // (optional)
                .one()
                .two()
                .build();
    }

    @lombok.Value
    @lombok.Builder
    public static class TestData {
        private String one;
        private String two;
    }
}