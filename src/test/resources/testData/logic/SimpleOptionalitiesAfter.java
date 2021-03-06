public class Simple {
    public void method() {
        TestData data = TestData.builder()
                // (no_parameters)
                .withNoParams()
                // (required)
                .one()
                .three()
                .four()
                .whereIsFive()
                .seven()
                .nine()
                // (optional)
                .two()
                .six()
                .wrappersCanBeOptional()
                .eight()
                // (multiple_parameters)
                .withManyParams() // (required, optional, required)
                .build();
    }

    @lombok.Value
    @lombok.Builder(builderClassName = "Builder")
    public static class TestData {
        @javax.validation.constraints.NotNull
        private String one;
        private String two;
        private int three;
        @javax.validation.constraints.NotNull
        private Integer four;
        private String six;
        private boolean whereIsFive;
        private Integer wrappersCanBeOptional;
        @javax.validation.constraints.NotNull
        private String seven;
        private String eight;
        private boolean nine;

        public static class Builder {
            public Builder withNoParams() {
                return this;
            }

            public Builder withManyParams(String seven, String eight, boolean nine) {
                return this;
            }
        }
    }
}