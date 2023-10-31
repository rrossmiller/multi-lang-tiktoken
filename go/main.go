package main

import (
	"fmt"
	"os"
	"strings"
	"time"

	"github.com/pkoukk/tiktoken-go"
)

func main() {
	runs := 50
	encoding := "gpt-3.5-turbo"
	f, err := os.ReadFile("../input.txt")
	if err != nil {
		panic(err)
	}
	data := strings.Split(string(f), "\n\n")
	fmt.Printf("Num samples: %d\n", len(data))

	tkm, err := tiktoken.EncodingForModel(encoding)
	if err != nil {
		panic(err)
	}

	// encode
	times := []int64{}
	allResults := [][]int{}
	for i := 0; i < runs; i++ {
		start := time.Now()
		results := make([]int, len(data))
		for j, d := range data {
			tokens := tkm.Encode(d, nil, nil)
			results[j] = len(tokens)
		}
		end := time.Since(start).Nanoseconds()
		times = append(times, end)
		allResults = append(allResults, results)
	}

	avgTime := mean(times) / 1e9
	fmt.Printf("Avg Elapsed: %v seconds\n", avgTime)
	allResultsMean := mean2d(allResults)

	//  write results baseline csv
	var sb strings.Builder
	sb.WriteString(fmt.Sprintf("Num Samples: %v\nAvg: %v seconds\n", len(data), avgTime))
	for i, r := range allResultsMean {
		if i == len(allResultsMean)-1 {
			sb.WriteString(fmt.Sprintf("%v", r))
		} else {
			sb.WriteString(fmt.Sprintf("%v,", r))
		}

	}
	sb.WriteString("\n")
	for i, r := range times {
		if i == len(times)-1 {
			sb.WriteString(fmt.Sprintf("%v", r))
		} else {
			sb.WriteString(fmt.Sprintf("%v,", r))
		}

	}
	os.WriteFile("results.txt", []byte(sb.String()), 0644)
}

func mean(a []int64) float64 {
	sum := int64(0)
	for _, i := range a {
		sum += i
	}

	return float64(sum) / float64(len(a))
}

func mean2d(a [][]int) []float64 {
	t := transpose(a)
	means := []float64{}
	for _, i := range t {
		means = append(means, mean(i))
	}

	return means
}
func transpose(slice [][]int) [][]int64 {
	xl := len(slice[0])
	yl := len(slice)
	result := make([][]int64, xl)
	for i := range result {
		result[i] = make([]int64, yl)
	}
	for i := 0; i < xl; i++ {
		for j := 0; j < yl; j++ {
			result[i][j] = int64(slice[j][i])
		}
	}
	return result
}
