package com.github.gridlts.tbtasksync

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TbtasksyncApplication implements CommandLineRunner {

	static void main( String[] args) {
		SpringApplication.run(TbtasksyncApplication, args)
	}

	public void run(String... args) {
		println("EXECUTING : Thunderbird tasks sync");
	}

}
