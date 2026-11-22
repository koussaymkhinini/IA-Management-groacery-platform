
import {
    ResponsiveContainer,
    BarChart,
    Bar,
    LineChart,
    Line,
    PieChart,
    Pie,
    Cell,
    CartesianGrid,
    XAxis,
    YAxis,
    Tooltip,
    Legend
} from "recharts";

function ChartRenderer({ chart }) {
    if (!chart || !chart.data || chart.data.length === 0) {
        return null;
    }

    const { title, xKey, yKey, data } = chart;

    // Normalize chart type received from the backend.
    const type = (chart.type || "bar").trim().toLowerCase();
    const COLORS = [
        "#6366f1",
        "#06b6d4",
        "#10b981",
        "#f59e0b",
        "#ef4444",
        "#8b5cf6",
        "#ec4899",
        "#14b8a6"
    ];

    const tooltipStyle = {
        backgroundColor: "#1e293b",
        border: "1px solid #64748b",
        borderRadius: "10px",
        color: "#ffffff",
        boxShadow: "0 4px 12px rgba(0,0,0,0.3)"
    };

    const renderAxes = () => (
        <>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey={xKey} />
            <YAxis />
            <Tooltip
                contentStyle={tooltipStyle}
                labelStyle={{ color: "#cbd5e1" }}
                itemStyle={{ color: "#ffffff" }}
            />
            <Legend />
        </>
    );

    return (
        <div className="chart-container">
            {title && <h3>{title}</h3>}

            <ResponsiveContainer width="100%" height={350}>
                {type === "pie" ? (
                    <PieChart>
                        <Pie
                            data={data}
                            dataKey={yKey}
                            nameKey={xKey}
                            cx="50%"
                            cy="50%"
                            outerRadius={120}
                            label
                        >
                            {data.map((entry, index) => (
                                <Cell
                                    key={`cell-${index}`}
                                    fill={COLORS[index % COLORS.length]}
                                />
                            ))}
                        </Pie>
                        <Tooltip contentStyle={tooltipStyle} />
                        <Legend />
                    </PieChart>
                ) : type === "line" ? (
                    <LineChart data={data}>
                        {renderAxes()}
                        <Line
                            type="monotone"
                            dataKey={yKey}
                            stroke="#6366f1"
                            strokeWidth={3}
                            dot={{
                                r: 4,
                                fill: "#6366f1",
                                stroke: "#ffffff",
                                strokeWidth: 1
                            }}
                            activeDot={{
                                r: 8,
                                fill: "#f59e0b",
                                stroke: "#ffffff",
                                strokeWidth: 3
                            }}
                        />
                    </LineChart>
                ) : (
                    <BarChart data={data}>
                        {renderAxes()}
                        <Bar
                            dataKey={yKey}
                            fill="#6366f1"
                            activeBar={{
                                fill: "#a5b4fc",
                                stroke: "#ffffff",
                                strokeWidth: 2
                            }}
                            cursor="pointer"
                        />
                    </BarChart>
                )}
            </ResponsiveContainer>
        </div>
    );
}

export default ChartRenderer;
