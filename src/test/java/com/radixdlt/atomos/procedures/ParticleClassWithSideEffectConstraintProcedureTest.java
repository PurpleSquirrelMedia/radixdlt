package com.radixdlt.atomos.procedures;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.radixdlt.atomos.Result;
import com.radixdlt.atoms.Particle;
import com.radixdlt.atoms.ParticleGroup;
import com.radixdlt.atoms.SpunParticle;
import com.radixdlt.common.EUID;
import com.radixdlt.constraintmachine.AtomMetadata;
import com.radixdlt.constraintmachine.ProcedureError;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ParticleClassWithSideEffectConstraintProcedureTest {
	private abstract static class MyParticle extends Particle {
	}

	private abstract static class SideEffectParticle extends Particle {
	}

	@Test
	public void testSatisfiedParticleClassWithSideEffect() {
		MyParticle particle = mock(MyParticle.class);
		SideEffectParticle sideEffect = mock(SideEffectParticle.class);

		ParticleGroup group = ParticleGroup.of(
			SpunParticle.up(particle),
			SpunParticle.up(sideEffect)
		);

		ParticleClassWithSideEffectConstraintProcedure<MyParticle, SideEffectParticle> procedure =
			new ParticleClassWithSideEffectConstraintProcedure<>(MyParticle.class, SideEffectParticle.class,
				(p, s, metadata) -> {
					assertEquals(particle, p);
					assertEquals(sideEffect, s);

					return Result.success();
				});

		procedure.validate(group, mock(AtomMetadata.class));
	}

	@Test
	public void testUnsatisfiedParticleClassWithSideEffect() {
		MyParticle particle = mock(MyParticle.class);
		SideEffectParticle sideEffect = mock(SideEffectParticle.class);

		when(particle.getHID()).thenReturn(EUID.ONE);
		when(sideEffect.getHID()).thenReturn(EUID.TWO);

		ParticleGroup pg = ParticleGroup.of(
			SpunParticle.up(particle),
			SpunParticle.up(sideEffect)
		);

		ParticleClassWithSideEffectConstraintProcedure<MyParticle, SideEffectParticle> procedure =
			new ParticleClassWithSideEffectConstraintProcedure<>(MyParticle.class, SideEffectParticle.class,
				(p, s, metadata) -> {
					assertEquals(particle, p);
					assertEquals(sideEffect, s);

					return Result.error("some error");
				});

		Stream<ProcedureError> issues = procedure.validate(pg, mock(AtomMetadata.class));
		Assertions.assertThat(issues).anyMatch(i ->
			i.getErrMsg().contains("some error")
				&& i.getErrMsg().contains(particle.getHID().toString())
				&& i.getErrMsg().contains(sideEffect.getHID().toString())
		);
	}

	@Test
	public void testUnsatisfiedParticleClassWithMissingSideEffect() {
		MyParticle particle = mock(MyParticle.class);

		when(particle.getHID()).thenReturn(EUID.ONE);

		ParticleGroup pg = ParticleGroup.of(
			SpunParticle.up(particle)
		);

		ParticleClassWithSideEffectConstraintProcedure<MyParticle, SideEffectParticle> procedure =
			new ParticleClassWithSideEffectConstraintProcedure<>(MyParticle.class, SideEffectParticle.class,
				(p, s, metadata) -> {
					throw new AssertionError("Check should never be called when side effect is missing.");
				});

		Stream<ProcedureError> issues = procedure.validate(pg, mock(AtomMetadata.class));
		Assertions.assertThat(issues).anyMatch(i ->
			i.getErrMsg().contains("Unsatisfied")
				&& i.getErrMsg().contains(particle.getHID().toString())
		);
	}
}