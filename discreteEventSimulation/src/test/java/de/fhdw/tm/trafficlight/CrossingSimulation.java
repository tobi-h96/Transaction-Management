package de.fhdw.tm.trafficlight;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.fhdw.tm.des.modelling.ModelProcess;
import de.fhdw.tm.des.scheduler.DESScheduler;
import de.fhdw.tm.des.scheduler.Simulation;
import de.fhdw.tm.des.scheduler.SimulationResult;
import de.fhdw.tm.des.scheduler.Simulator;

public class CrossingSimulation {
	public static void main(String[] args) {

		DESScheduler.setDebug(true);

		Simulator simulator = new Simulator(42);

		Simulation sim = new Simulation() {

			@Override
			public void start() {
				System.out.println("Start!");
			}

			@Override
			public void injectStart() {
				DESScheduler.scheduleToFuture(new ModelProcess(new Crossing(4, 20, 5, 2)), 0);
				DESScheduler.scheduleToFuture(() -> DESScheduler.terminate(), 1000);
			}

			@Override
			public void finish() {
				System.out.println("End!");
			}
		};

		for (int i = 0; i < 1; i++) {
			simulator.simulate(sim);
		}
		simulator.terminate();
		try {
			List<SimulationResult> simulationResult = simulator.readResults();
			simulationResult.forEach(x -> x.printResults());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
