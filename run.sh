clear
echo "Python"
cd py
python3 main.py

echo
echo "Go"
cd ../go
go run .

echo
echo "Rust"
cd ../rust
cargo run --quiet --release

echo
echo "Java"
echo "TODO"
