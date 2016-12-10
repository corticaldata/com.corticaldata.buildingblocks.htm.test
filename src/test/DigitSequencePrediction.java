package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

import org.numenta.nupic.Parameters;
import org.numenta.nupic.Parameters.KEY;
import org.numenta.nupic.algorithms.CLAClassifier;
import org.numenta.nupic.algorithms.Classification;
import org.numenta.nupic.algorithms.SpatialPooler;
import org.numenta.nupic.algorithms.TemporalMemory;
import org.numenta.nupic.encoders.ScalarEncoder;
import org.numenta.nupic.model.ComputeCycle;
import org.numenta.nupic.model.Connections;
import org.numenta.nupic.util.ArrayUtils;
import org.numenta.nupic.util.FastRandom;

import gnu.trove.list.array.TIntArrayList;

/**
 * Ejemplo de aprendizaje de secuencias de dígitos y predicción del siguiente dígito
 * utilizando Memoria Temporal Jerárquica (HTM).
 * 
 * @author paco
 */
public class DigitSequencePrediction {
	public static void main(String[] args) throws Exception {
		
		Parameters parameters = Parameters.getAllDefaultParameters();
		
		parameters.set(KEY.INPUT_DIMENSIONS, new int[] { 10 });
		parameters.set(KEY.COLUMN_DIMENSIONS, new int[] { 20 });
		parameters.set(KEY.CELLS_PER_COLUMN, 6);
		
		// Spatial Pooler specific
		parameters.set(KEY.POTENTIAL_RADIUS, 12);
		parameters.set(KEY.POTENTIAL_PCT, 0.5);
		parameters.set(KEY.GLOBAL_INHIBITION, false);
		parameters.set(KEY.LOCAL_AREA_DENSITY, -1.0);
		parameters.set(KEY.NUM_ACTIVE_COLUMNS_PER_INH_AREA, 5.0);
		parameters.set(KEY.STIMULUS_THRESHOLD, 1.0);
		parameters.set(KEY.SYN_PERM_INACTIVE_DEC, 0.0005);
		parameters.set(KEY.SYN_PERM_ACTIVE_INC, 0.0015);
		parameters.set(KEY.SYN_PERM_TRIM_THRESHOLD, 0.05);
		parameters.set(KEY.SYN_PERM_CONNECTED, 0.1);
		parameters.set(KEY.MIN_PCT_OVERLAP_DUTY_CYCLES, 0.1);
		parameters.set(KEY.MIN_PCT_ACTIVE_DUTY_CYCLES, 0.1);
		parameters.set(KEY.DUTY_CYCLE_PERIOD, 10);
		parameters.set(KEY.MAX_BOOST, 10.0);
		parameters.set(KEY.SEED, 42);
		
		// Temporal Memory specific
		parameters.set(KEY.INITIAL_PERMANENCE, 0.2);
		parameters.set(KEY.CONNECTED_PERMANENCE, 0.8);
		parameters.set(KEY.MIN_THRESHOLD, 5);
		parameters.set(KEY.MAX_NEW_SYNAPSE_COUNT, 6);
		parameters.set(KEY.PERMANENCE_INCREMENT, 0.1);
		parameters.set(KEY.PERMANENCE_DECREMENT, 0.1);
		parameters.set(KEY.ACTIVATION_THRESHOLD, 4);
		
		parameters.set(KEY.RANDOM, new FastRandom());
		
		ScalarEncoder encoder = ScalarEncoder
				.builder()
				.n(10)
				.w(3)
				.radius(1.0)
				.minVal(0.0)
				.maxVal(10.0)
				.periodic(true)
				.forced(true)
				.resolution(1)
				.build();
		
		SpatialPooler spatialPooler = new SpatialPooler();
		TemporalMemory temporalMemory = new TemporalMemory();
		CLAClassifier classifier = new CLAClassifier(new TIntArrayList(new int[] {1}), 0.1, 0.3, 0);
		
		Connections memory = new Connections();
		
		Map<String, Object> classification = new LinkedHashMap<String, Object>();
		
		parameters.apply(memory);
		spatialPooler.init(memory);
		TemporalMemory.init(memory);
		
		int columnCount = memory.getPotentialPools().getMaxIndex() + 1;
		
		System.out.println("Input numbers from 0 to 9:");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		int count = 0;
		while (true) {
			count++;
			double value = Double.parseDouble(br.readLine());
			
			int[] output = new int[columnCount];
			
			// Input through encoder
			int[] encoding = encoder.encode(value);
			//System.out.println("ScalarEncoder Output = " + Arrays.toString(encoding));
			int bucketIdx = encoder.getBucketIndices(value)[0];
			
			// Input through spatial pooler
			spatialPooler.compute(memory, encoding, output, true);
			//System.out.println("SpatialPooler Output = " + Arrays.toString(output));
			
			// Input through temporal memory
			int[] input = ArrayUtils.where(output, ArrayUtils.WHERE_1);
			ComputeCycle cc = temporalMemory.compute(memory, input, true);
			
			// Get the active cells for classifier input
			int[] activeCellIndexes = Connections.asCellIndexes(cc.activeCells()).stream().mapToInt(p -> p).sorted().toArray();
			//System.out.println("TemporalMemory Input = " + Arrays.toString(input));
			
			classification.put("bucketIdx", bucketIdx);
			classification.put("actValue", value);
			
			Classification<Double> result = classifier.compute(count, classification, activeCellIndexes, true, true);
			System.out.println("Prediction: " + new BigDecimal(result.getMostProbableValue(1)).setScale(3, RoundingMode.HALF_EVEN));
		}
	}
}
