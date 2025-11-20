from pathlib import Path, PurePath
import argparse
import itertools

def main():
    parser = argparse.ArgumentParser(
        prog='hex2ascii',
        description='What the program does',
    )
    parser.add_argument("-o", "--output", default=None)
    args = parser.parse_args()

    if args.output is None:
        print("--output is not set, not files will be generated.")
    print("Input format: (H - Hex Digit)")
    if args.output is None:
        print(" - H...")
    print(" - HHHH:H...")
    print(" - HHHHHH:H...")
    print("H... encodes the pixel data")
    print("HHHHH(HH) 4 or 6 digit Unicode code point in Hexadecimal.")

    while True:
        unifont_hex = input(" > ")
        if len(unifont_hex) == 0:
            break

        match unifont_hex.split(":", 2):
            case [name, data]:
                if args.output is not None:
                    path = Path(args.output, f"{name}.txt")
                    path.parent.mkdir(parents=True, exist_ok=True)
                    path.write_text("\n".join("".join(line) for line in ascii_unifont(data)))
                    print("Wrote to", PurePath("{output}", f"{name}.txt"))
                else:
                    print(f" --- {name} --- ")
                    print_unifont(data)
                    print()
            case [data]:
                print(" --- (unassigned) --- ")
                print_unifont(data)
                print()
    print("Exiting...")

def print_unifont(data):
    for r in ascii_unifont(data):
        print(*r, sep='')

def ascii_unifont(data):
    # data = "".join(data)
    # print("DATA:", data)
    return itertools.batched(
        (
            '@' if b == '1' else '.'
            for b in itertools.chain.from_iterable(
                format(int(h, 16), '04b')
                for h in data
            )
        ),
        16
    )

if __name__ == "__main__":
    main()
