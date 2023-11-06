import http from "k6/http";
import { check, sleep } from "k6";

// Test configuration
export const options = {
    vus: 20,
    thresholds: {
        // Assert that 99% of requests finish within 3000ms.
        http_req_duration: ["p(99) < 3000"],
    },
    // Ramp the number of virtual users up and down
    stages: [
        { duration: "20s", target: 30 },
        { duration: "20s", target: 40 },
        { duration: "20s", target: 0 },
    ],
};

// Simulated user behavior

export function setup() {
    console.log("setup")
    const url = 'http://localhost:9001/item';
    const payload = JSON.stringify({
        storeId: '1',
        name: 'foo item',
        price: 1000,
        stock: 1000000,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.post(url, payload, params);
    return res.json()
}

export default function (data) {
    const url = 'http://localhost:9001/api/orders';
    const payload = JSON.stringify({
        userId: '1',
        itemList: [
            {
                id: data.id,
                quantity: '2',
            }
        ]
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let res = http.post(url, payload, params);
    check(res, { "status was 200": (r) => r.status === 200 });
}

export function teardown(data) {
    console.log("teardown")
    const res = http.get('http://localhost:9001/item/' + data.id)
}

