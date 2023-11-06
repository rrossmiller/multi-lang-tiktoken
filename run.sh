N=100
clear
echo "Python"

cd py
python3 main.py $N

echo
echo "Go"
cd ../go
go run . $N
go test

echo
echo "Rust"
cd ../rust
cargo run --quiet --release -- $N
cargo test

echo
echo "Java"
echo "TODO"
