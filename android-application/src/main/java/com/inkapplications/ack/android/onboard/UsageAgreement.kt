package com.inkapplications.ack.android.onboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.inkapplications.ack.android.ui.theme.AprsTheme

const val AGREEMENT_REVISION = 1

@Composable
fun UsageAgreement(
    controller: UserAgreementController,
) {
    Column(
        modifier = Modifier.padding(AprsTheme.spacing.gutter).verticalScroll(rememberScrollState()),
    ) {
        Text(
            "Usage and Privacy Agreement",
            style = AprsTheme.typography.h1,
        )
        Spacer(Modifier.height(AprsTheme.spacing.content))
        Text(
            "Legally Restricted Services",
            style = AprsTheme.typography.h2,
        )
        Spacer(Modifier.height(AprsTheme.spacing.item))
        Text(
            "This software provides access and tools for the amateur radio APRS network, a legally regulated service."
        )
        Text(
            "Some of the functionality provided by this application may require a license or be restricted in your area. It is your responsibility to research and follow local laws when using this software."
        )
        Spacer(Modifier.height(AprsTheme.spacing.content))
        Text(
            "Data Privacy",
            style = AprsTheme.typography.h2,
        )
        Spacer(Modifier.height(AprsTheme.spacing.item))
        Text(
            "This application can be used to transmit your location and other information to the APRS radio network. When transmitted, the information you provide, including but not limited to your location, will be transmitted publicly, either by radio or by an internet service, and should not be considered private."
        )
        Spacer(Modifier.height(AprsTheme.spacing.item))
        Text(
            "This application collects usage data using Google Firebase. Information collected may include information unique to your device, information about actions performed within the application, and logging data related to using the application. This information is used internally to improve the application and is not shared with any 3rd parties."
        )
        Spacer(Modifier.height(AprsTheme.spacing.content))
        Text(
            "No Warranty",
            style = AprsTheme.typography.h2,
        )
        Spacer(Modifier.height(AprsTheme.spacing.item))
        Text(
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = controller::onTermsDeclineClick,
            colors = ButtonDefaults.outlinedButtonColors(),
            modifier = Modifier.fillMaxWidth().padding(top = AprsTheme.spacing.content),
        ) {
            Text("Decline")
        }
        Spacer(Modifier.height(AprsTheme.spacing.item))
        Button(
            onClick = controller::onTermsAgreeClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("I Understand and Agree")
        }
    }
}
