# Offline Online Judge Simulator

## How to Run
1. Ensure you have `gcc`, `g++`, and `jdk` installed.
2. Run `./run.sh` from the terminal.

## Features
- **Code Editor**: Supports C, C++, and Java with syntax templates.
- **Custom Input**: Test your code with arbitrary input data directly within the problem viewer.
- **Judge System**: Compiles and executes code against test cases with concise status feedback.
- **Verdicts**: 
  - ACCEPTED (AC)
  - WRONG ANSWER (WA)
  - COMPILATION ERROR (CE)
  - RUNTIME ERROR (RE)
  - TIME LIMIT EXCEEDED (TLE) (2s limit)

## Adding Problems
1. Create a folder in `problems/` with the problem ID (e.g., `problems/B`).
2. Add a `description.txt` file containing the problem statement.
3. Create `input/` and `output/` subdirectories.
4. Add input files in `input/` (e.g., `1.in`, `2.in`).
5. Add expected output files in `output/` (e.g., `1.out`, `2.out`).
   - Note: The filenames must match (e.g., `1.in` corresponds to `1.out`).

## Project Structure
- `src/`: Source code.
- `problems/`: Problem data.
- `submissions/`: User submitted code (auto-generated).
- `bin_exec/`: Compiled user binaries (auto-generated).

## Deployment (How to Distribute)
To package the application for other users:
1. Run `./deploy.sh`.
2. This creates a `dist/` folder containing:
   - `OfflineOj.jar`: The executable application.
   - `problems/`: The folder containing problem data.
3. Zip or share the `dist` folder.
4. Users can run it via: `java -jar OfflineOj.jar`
5. **Requirements**: The user's machine must have:
   - Java Runtime (JRE/JDK)
   - GCC and G++ (for compiling C/C++ submissions) added to system PATH.
  
Still working---
