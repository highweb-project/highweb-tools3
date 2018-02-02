/**
 * 
 */
function fpslookup(fps) {
    console.log("send fps " + fps);
}

function flopslookup(flops) {
    // MFLOPS
    flops = Math.round(flops / (1000 * 1000));
    console.log("send flops " + flops);
}