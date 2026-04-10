package com.y271727uy.ranch_festival.dimension;

public final class DimensionDefinitions {
	public static final DimensionDefinition TEST = DimensionFestivalFactoryKt.TEST;

	private DimensionDefinitions() {
	}

	public static void bootstrap() {
		DimensionFestivalFactoryKt.bootstrap();
	}
}

