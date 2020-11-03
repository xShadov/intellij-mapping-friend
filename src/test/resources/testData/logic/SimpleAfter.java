public class Simple {
    public void method() {
        TestData data = TestData.builder()
                .one() // (required)
                .two() // (optional)
                .three() // (required)
                .four() // (required)
                .six() // (optional)
                .whereIsFive() // (required)
                .build();
    }

    @lombok.Value
    @lombok.Builder
    public static class TestData {
        @javax.validation.constraints.NotNull
        private String one;
        private String two;
        private int three;
        @javax.validation.constraints.NotNull
        private Integer four;
        private String six;
        private boolean whereIsFive;
    }
}