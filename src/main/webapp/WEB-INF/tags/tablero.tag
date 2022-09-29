<%@ attribute name="tablero" required="false" rtexprvalue="true" type="org.springframework.samples.endofline.tablero.Tablero"
 description="Board to be rendered" %>
<canvas id="canvas" width="${tablero.width}" height="${tablero.height}"></canvas>
<img id="source" src="${tablero.background}" style="display:none">
<script>
var canvas = document.getElementById("canvas");
var ctx = canvas.getContext("2d");
var image = document.getElementById('source');

ctx.drawImage(image, 0, 0, ${tablero.width}, ${tablero.height});
</script>
