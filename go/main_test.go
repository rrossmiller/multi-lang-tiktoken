package main

import (
	"fmt"
	"os"
	"strconv"
	"strings"
	"testing"
)

func TestIntMinBasic(t *testing.T) {
	if _, err := os.Stat("results.txt"); os.IsNotExist(err) {
		main()
	}
	if _, err := os.Stat("../py/baseline.txt"); os.IsNotExist(err) {
		t.Error("\npy/baseline.txt file does not exist.\nPlease run the main.py to generate the baseline.")
	}

	f, err := os.ReadFile("results.txt")
	if err != nil {
		t.Error(err)
	}
	tokens := strings.Split(strings.Split(string(f), "\n")[2], ",")
	f, err = os.ReadFile("../py/baseline.txt")
	if err != nil {
		t.Error(err)
	}
	baselineTokens := strings.Split(strings.Split(string(f), "\n")[2], ",")

	if len(tokens) != len(baselineTokens) {
		t.Errorf("A different number of test cases were used. %v != %v", len(tokens), len(baselineTokens))
	}

	for i := range tokens {
		tknCount, err := strconv.Atoi(strings.TrimSpace(tokens[i]))
		if err != nil {
			panic(err)
		}
		baselineTokenCount, err := strconv.Atoi(strings.TrimSpace(baselineTokens[i]))
		if err != nil {
			panic(err)
		}
		if tknCount != baselineTokenCount {
			t.Errorf("\nTest case %v failed. %v != %v", i, tokens[i], baselineTokens[i])
		}
	}
	fmt.Print("All test cases produce the same number of tokens.\n\n")
}
