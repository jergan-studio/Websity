const output = document.getElementById('output');
const helloButton = document.getElementById('helloButton');

helloButton.addEventListener('click', async () => {
    output.textContent = 'Calling Java...';

    try {
        const response = await fetch('/api/hello');
        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const data = await response.json();
        output.textContent = data.message;
    } catch (error) {
        output.textContent = `Error: ${error.message}`;
    }
});
