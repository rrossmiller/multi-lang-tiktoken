use std::{env, fs, time::Instant};
use tiktoken_rs::cl100k_base;
fn main() {
    // get runs from args
    let args = env::args().collect::<Vec<String>>();
    let runs = args[1].parse::<usize>().expect("error parsing runs");

    let data_path = "../input.txt";
    let data = fs::read_to_string(data_path).expect(format!("{data_path} not found").as_str());
    let test_cases: Vec<&str> = data.split("\n\n").collect();

    let encoder = cl100k_base().expect("error loading clk100_base encoder");
    let mut times = Vec::new();
    let mut all_results = Vec::new();
    for _ in 0..runs {
        let mut results = Vec::new();
        // let start = Instant::now();
        for d in test_cases.iter() {
            let start = Instant::now();
            let tokens = encoder.encode_with_special_tokens(d).len();
            let elapsed = start.elapsed().as_nanos();
            times.push(elapsed);
            results.push(tokens)
        }
        // let elapsed = start.elapsed().as_nanos();
        // times.push(elapsed);
        all_results.push(results)
    }

    // let avg_time = mean_times(&times) / 1e9;
    // println!("Avg Elapsed: {} seconds", avg_time);
    let avg_time = mean_times(&times);
    println!("Avg Elapsed: {} nano seconds", avg_time);

    let all_results = mean_2d(all_results);

    // write results file
    let mut results_string = String::new();
    results_string.push_str(&format!("Num samples: {}\n", test_cases.len()));
    results_string.push_str(&format!("Avg {} seconds\n", avg_time));

    for (i, r) in all_results.iter().enumerate() {
        if i == all_results.len() - 1 {
            results_string.push_str(&format!("{}", r))
        } else {
            results_string.push_str(&format!("{},", r))
        }
    }
    results_string.push('\n');

    for (i, r) in times.iter().enumerate() {
        if i == all_results.len() - 1 {
            results_string.push_str(&format!("{}", r))
        } else {
            results_string.push_str(&format!("{},", r))
        }
    }

    fs::write("results.txt", results_string).expect("error writing results file");
}

fn mean_times(v: &Vec<u128>) -> f64 {
    let sum: u128 = v.iter().sum();

    sum as f64 / v.len() as f64
}

fn mean(v: Vec<i32>) -> f64 {
    let sum: i32 = v.iter().sum();
    sum as f64 / v.len() as f64
}

fn mean_2d(v: Vec<Vec<usize>>) -> Vec<f64> {
    let t = transpose(v);

    let mut means = Vec::new();
    for i in t.iter() {
        means.push(mean(i.to_vec()));
    }
    means
}

fn transpose(v: Vec<Vec<usize>>) -> Vec<Vec<i32>> {
    let xl = v[0].len();
    let yl = v.len();

    let mut result: Vec<Vec<i32>> = Vec::with_capacity(xl);
    for _ in 0..xl {
        result.push(Vec::new());
    }
    for i in 0..xl {
        for j in 0..yl {
            result[i].push(v[j][i] as i32);
        }
    }

    result
}

#[cfg(test)]
mod tests {
    use std::fs;

    use crate::transpose;

    #[test]
    fn test_transpose() {
        let input = vec![vec![0, 1, 2], vec![2, 3, 4]];
        let output = vec![vec![0, 2], vec![1, 3], vec![2, 4]];

        let input = transpose(input);
        for i in 0..input.len() {
            for j in 0..input[i].len() {
                assert_eq!(input[i][j], output[i][j])
            }
        }
    }

    #[test]
    fn test_tokens_match() {
        let data_path = "results.txt";
        let data = fs::read_to_string(data_path).expect(format!("{data_path} not found").as_str());
        let data = data.lines().collect::<Vec<&str>>()[2]
            .split(',')
            .map(|d| d.parse().unwrap())
            .collect::<Vec<i32>>();

        let data_path = "../py/baseline.txt";
        let base_data =
            fs::read_to_string(data_path).expect(format!("{data_path} not found").as_str());
        let base_data = base_data.lines().collect::<Vec<&str>>()[2]
            .split(',')
            .map(|d| d.parse().unwrap())
            .collect::<Vec<i32>>();

        assert_eq!(data.len(), base_data.len());
        for i in 0..data.len() {
            assert_eq!(data[i], base_data[i])
        }
    }
}
