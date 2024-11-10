<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <h1>Olympic Runner Selection System</h1>
    <p>
        This project implements a Java-based system for managing and analyzing race times of runners in preparation for an Olympic selection. The program supports efficient operations on a dynamic data structure to track runners' race data and provides various functionalities for calculating performance metrics.
    </p>
    <h2>Project Features</h2>
    <ul>
        <li><strong>Initialize Race:</strong> Set up the system for managing runners and their race times.</li>
        <li><strong>Add/Remove Runner:</strong> Dynamically add or remove runners based on unique identifiers.</li>
        <li><strong>Record/Remove Race Time:</strong> Log race times for runners and remove specific race entries if needed.</li>
        <li><strong>Retrieve Time Metrics:</strong> Calculate minimum and average race times for each runner.</li>
        <li><strong>Ranking:</strong> Determine rankings based on minimum or average times, including finding the fastest runner overall.</li>
    </ul>
    <h2>Challenges Addressed</h2>
    <p>
        The key challenges in this project involve handling a dynamic set of runners and race data efficiently. Each function was designed with performance in mind, ensuring that operations like adding, removing, and querying data are optimized with time complexity considerations. Error handling is also implemented to manage cases such as duplicate runners or invalid race times.
    </p>
    <h2>How to Use</h2>
    <p>
        To use this system, compile and run the provided Java files. The main functionality is encapsulated in the <code>Race</code> class, which serves as the primary data structure for managing runners. Use the <code>main</code> class to test the functions and explore the system capabilities.
    </p>
    <h2>Project Files</h2>
    <ul>
        <li><code>Race.java</code>: Main class implementing the race management system.</li>
        <li><code>main.java</code>: Entry point to run and test the race system.</li>
        <li><code>RunnerID.java</code>: Abstract class representing unique identifiers for runners.</li>
    </ul>
    <h2>Summary</h2>
    <p>
        This project offers a structured and optimized approach to handling large sets of runner data for Olympic selection purposes. By balancing performance and functionality, the system provides a comprehensive solution for tracking and analyzing race times in a competitive setting.
    </p>
</body>
</html>
