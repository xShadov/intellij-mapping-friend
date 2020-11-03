public class Complex {
    public void method() {
        TestData data = TestData.builder()<caret>
    }

    @lombok.Value
    @lombok.Builder
    public static class TestData {
        @javax.validation.constraints.NotNull
        private OtherData otherData;
        private OtherData otherDataOptional;
    }

    public static class OtherData {

    }
}